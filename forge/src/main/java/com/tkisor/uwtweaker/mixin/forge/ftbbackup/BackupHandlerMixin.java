package com.tkisor.uwtweaker.mixin.forge.ftbbackup;

import net.creeperhost.ftbbackups.BackupHandler;
import net.creeperhost.ftbbackups.FTBBackups;
import net.creeperhost.ftbbackups.config.Config;
import net.creeperhost.ftbbackups.config.Format;
import net.creeperhost.ftbbackups.data.Backup;
import net.creeperhost.ftbbackups.utils.FileUtils;
import net.creeperhost.ftbbackups.utils.TieredBackupTest;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.spongepowered.asm.mixin.*;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static net.creeperhost.ftbbackups.BackupHandler.*;

@Mixin(value = BackupHandler.class, remap = false)
public abstract class BackupHandlerMixin {
    @Shadow private static Path serverRoot;
    @Shadow private static Path backupFolderPath;
    @Shadow private static Path worldFolder;
    @Shadow @Final private static AtomicBoolean backupFailed;
    @Shadow private static AtomicReference<String> backupPreview;
    @Shadow private static String failReason;
    @Shadow private static long lastAutoBackup;


    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void createBackup(MinecraftServer minecraftServer, boolean protect, String name) {
        try {
            if (FTBBackups.isShutdown || !Config.cached().enabled) return;

            if (Config.cached().only_if_players_been_online && !BackupHandler.isDirty) {
                FTBBackups.LOGGER.info("Skipping backup, no players have been online since last backup.");
                return;
            }
            worldFolder = minecraftServer.getWorldPath(LevelResource.ROOT).toAbsolutePath();
            FTBBackups.LOGGER.info("Found world folder at " + worldFolder);
            String backupName = TieredBackupTest.getBackupName();

            Path backupLocation = backupFolderPath.resolve(backupName);
            Format format = Config.cached().backup_format;

            if (canCreateBackup()) {
                lastAutoBackup = TieredBackupTest.getBackupTime();

                backupRunning.set(true);
                //Force save all the chunk and player data
                CompletableFuture<?> saveOp = minecraftServer.submit(() -> {
                    if (!minecraftServer.isCurrentlySaving()) {
                        minecraftServer.saveEverything(true, false, true);
                    }
                });
                //Set the worlds not to save while we are creating a backup
                setNoSave(minecraftServer, true);
                //Store the time we started the backup
                AtomicLong startTime = new AtomicLong(System.nanoTime());
                //Store the finishTime outside the thread for later use
                AtomicLong finishTime = new AtomicLong();

                //Add the backup entry first so in the event the backup is interrupted we are not left with an orphaned partial backup that will never get cleared automatically.
                Backup backup = new Backup(worldFolder.normalize().getFileName().toString(), lastAutoBackup, backupLocation.toString(), 0, 0, "", backupPreview.get(), protect, name, format, false);
                addBackup(backup);
                updateJson();

                //Start the backup process in its own thread
                currentFuture = CompletableFuture.runAsync(() ->
                {
                    try {
                        //Ensure that save operation we just scheduled has completed before we continue.
                        if (!saveOp.isDone()) {
                            FTBBackups.LOGGER.info("Waiting for world save to complete.");
                            saveOp.get(30, TimeUnit.SECONDS);
                        }

                        //Warn all online players that the server is going to start creating a backup
                        alertPlayers(minecraftServer, Component.translatable(FTBBackups.MOD_ID + ".backup.starting"));
                        //Create the full path to this backup
                        Path backupPath = backupFolderPath.resolve(backupName);
                        //Create a zip of the world folder and store it in the /backup folder
                        List<Path> backupPaths = new LinkedList<>();
                        backupPaths.add(worldFolder);

                        try (Stream<Path> pathStream = Files.walk(serverRoot)) {
                            for (Path path : (Iterable<Path>) pathStream::iterator) {
                                if (Files.isDirectory(path)) continue;
                                Path relFile = serverRoot.relativize(path);
                                if (!FileUtils.matchesAny(relFile, Config.cached().additional_files)) continue;

                                try {
                                    if (!FileUtils.isChildOf(path, serverRoot)) {
                                        FTBBackups.LOGGER.warn("Ignoring additional file {}, as it is not a child of the server root directory.", relFile);
                                        continue;
                                    }

                                    if (FileUtils.isChildOf(path, worldFolder)) {
                                        FTBBackups.LOGGER.warn("Ignoring additional file {}, as it is a child of the world folder.", relFile);
                                        continue;
                                    }

                                    if (FileUtils.isChildOf(path, backupFolderPath)) {
                                        FTBBackups.LOGGER.warn("Ignoring additional file {}, as it is a child of the backups folder.", relFile);
                                        continue;
                                    }

                                    if (Files.exists(path)) {
                                        backupPaths.add(path);
                                    }
                                } catch (Exception err) {
                                    FTBBackups.LOGGER.error("Failed to add additional file '{}' to the backup.", relFile, err);
                                }
                            }
                        }

                        for (String p : Config.cached().additional_directories) {
                            try {
                                Path path = serverRoot.resolve(p);
                                if (!FileUtils.isChildOf(path, serverRoot)) {
                                    FTBBackups.LOGGER.warn("Ignoring additional directory {}, as it is not a child of the server root directory.", p);
                                    continue;
                                }

                                if (path.equals(worldFolder)) {
                                    FTBBackups.LOGGER.warn("Ignoring additional directory {}, as it is the world folder.", p);
                                    continue;
                                }

                                if (FileUtils.isChildOf(path, worldFolder)) {
                                    FTBBackups.LOGGER.warn("Ignoring additional directory {}, as it is a child of the world folder.", p);
                                    continue;
                                }
                                if (FileUtils.isChildOf(path, backupFolderPath)) {
                                    FTBBackups.LOGGER.warn("Ignoring additional directory {}, as it is a child of the backups folder.", p);
                                    continue;
                                }

                                if (!Files.isDirectory(path)) {
                                    FTBBackups.LOGGER.warn("Ignoring additional directory {}, as it is not a directory..", p);
                                    continue;
                                }

                                if (Files.exists(path)) {
                                    backupPaths.add(path);
                                }
                            } catch (Exception err) {
                                FTBBackups.LOGGER.error("Failed to add additional directory '{}' to the backup.", p, err);
                            }
                        }
                        backupPreview.set(createPreview(minecraftServer));
                        if (format == Format.ZIP) {
                            FileUtils.zip(backupPath, serverRoot, backupPaths);
                        } else {
                            FileUtils.copy(backupPath, serverRoot, backupPaths);
                        }
                        //The backup did not fail
                        backupFailed.set(false);
                        BackupHandler.isDirty = false;
                    } catch (Exception e) {
                        //Set backup running state to false
                        backupRunning.set(false);
                        //The backup failed to store it
                        backupFailed.set(true);
                        //Set alerts to all players on the server
                        alertPlayers(minecraftServer, Component.translatable(FTBBackups.MOD_ID + ".backup.failed"));
                        //Log and print stacktraces
                        FTBBackups.LOGGER.error("Failed to create backup", e);
                        if (e instanceof FileAlreadyExistsException) {
                            TieredBackupTest.testBackupCount++;
                        }
                    }
                }, FTBBackups.backupExecutor).thenRun(() ->
                {
                    currentFuture = null;
                    //Set world save state to false to allow saves again
                    setNoSave(minecraftServer, false);
                    //If the backup failed then we don't need to do anything
                    if (backupFailed.get()) {
                        //This reset should not be needed but making sure anyway
                        backupFailed.set(false);
                        backupRunning.set(false);
                        return;
                    }

                    finishTime.set(System.nanoTime());
                    //Workout the time it took to create the backup
                    long elapsedTime = finishTime.get() - startTime.get();
                    //Set backup running state to false
                    backupRunning.set(false);
                    //Alert players that backup has finished being created
                    alertPlayers(minecraftServer, Component.translatable(I18n.get("uwtweaker.ftbbackups2.backup.finished.end", format(elapsedTime) + (Config.cached().display_file_size ? " Size: " + FileUtils.getSizeString(backupLocation.toFile().length()) : ""))));
//                    alertPlayers(minecraftServer, Component.translatable("Backup finished in 准备改 " + format(elapsedTime) + (Config.cached().display_file_size ? " Size: " + FileUtils.getSizeString(backupLocation.toFile().length()) : "")));

                    String sha1;
                    float ratio = 1;
                    long backupSize = FileUtils.getSize(backupLocation.toFile());
                    if (format == Format.ZIP) {
                        //Get the sha1 of the new backup .zip to store to the json file
                        sha1 = FileUtils.getFileSha1(backupLocation);
                        //Do some math to figure out the ratio of compression
                        ratio = (float) backupSize / (float) FileUtils.getFolderSize(worldFolder.toFile());
                    } else {
                        sha1 = FileUtils.getDirectorySha1(backupLocation);
                    }

                    FTBBackups.LOGGER.info("Backup size " + FileUtils.getSizeString(backupLocation.toFile().length()) + " World Size " + FileUtils.getSizeString(FileUtils.getFolderSize(worldFolder.toFile())));
                    //Update backup entry data entry and mark it as complete.
                    backup.setRatio(ratio).setSha1(sha1).setComplete();
                    backup.setSize(backupSize);

                    updateJson();
                    FTBBackups.LOGGER.info("New backup created at " + backupLocation + " size: " + FileUtils.getSizeString(backupLocation) + " Took: " + format(elapsedTime) + " Sha1: " + sha1);

                    TieredBackupTest.testBackupCount++;
                });
            } else {
                //Create a new message for the failReason
                if (!failReason.isEmpty()) {
                    backupRunning.set(false);
                    String failMessage = "Unable to create backup, Reason: " + failReason;
                    //Alert players of the fail status using the message
                    alertPlayers(minecraftServer, Component.translatable(failMessage));
                    //Log the failMessage
                    FTBBackups.LOGGER.error(failMessage);
                    //Reset the fail to avoid confusion
                    failMessage = "";
                }
                backupRunning.set(false);
            }
        } catch (Exception e) {
            FTBBackups.LOGGER.error("An error occurred while running backup!", e);
            backupRunning.set(false);
        }
    }
}
