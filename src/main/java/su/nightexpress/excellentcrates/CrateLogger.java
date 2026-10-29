package su.nightexpress.excellentcrates;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.Reward;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * File logging follows 7.0 AsyncFileHistoryAppender: lines are queued on the main thread and written by one
 * worker thread that keeps the file open and flushes once the queue is empty (a mass open = one flush).
 */
public class CrateLogger {

    private final CratesPlugin          plugin;
    private final BlockingQueue<String> queue;

    private Thread           workerThread;
    private volatile boolean running;

    public CrateLogger(@NotNull CratesPlugin plugin) {
        this.plugin = plugin;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void logReward(@NotNull Player player, @NotNull Reward reward) {
        Crate crate = reward.getCrate();

        String text = player.getName() + " won " + reward.getName() +
            " (ID: " + reward.getId() + ", Weight: " + reward.getWeight() + ")" +
            " from " + crate.getName() + " (ID: " + crate.getId() + ")";

        this.log(text);
    }

    private void log(@NotNull String text) {
        if (!Config.LOGS_TO_CONSOLE.get() && !Config.LOGS_TO_FILE.get()) return;

        text = Colorizer.strip(NightMessage.asLegacy(text));

        if (Config.LOGS_TO_CONSOLE.get()) {
            this.plugin.info(text);
        }
        if (Config.LOGS_TO_FILE.get()) {
            String date = LocalDateTime.now().format(Config.LOGS_DATE_FORMAT.get());
            this.queue.offer("[" + date + "] " + text);
            this.startWorker();
        }
    }

    private synchronized void startWorker() {
        if (this.running) return;

        Path path = Path.of(this.plugin.getDataFolder().getPath(), Config.FILE_LOGS);
        this.running = true;
        this.workerThread = new Thread(() -> this.processQueue(path), "ExcellentCrates-Log-Writer");
        this.workerThread.setDaemon(true);
        this.workerThread.start();
    }

    private void processQueue(@NotNull Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            while (this.running || !this.queue.isEmpty()) {
                String line = this.queue.poll(100, TimeUnit.MILLISECONDS);
                if (line == null) continue;

                writer.write(line);
                writer.newLine();

                if (this.queue.isEmpty()) {
                    writer.flush();
                }
            }
            writer.flush();
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Writes what's left in the queue and stops the worker. Called on plugin disable/reload.
     */
    public synchronized void shutdown() {
        if (!this.running) return;

        this.running = false;
        try {
            this.workerThread.join(3000L);
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        this.workerThread = null;
    }
}
