package de.thiomains.jumpandrun;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private JumpAndRun jump;

    @Override
    public void onEnable() {
        jump = new JumpAndRun(this);
        jump.startScheduler();
        new JaRCommand(this);
    }

    public JumpAndRun getJumpAndRun() {
        return jump;
    }
}
