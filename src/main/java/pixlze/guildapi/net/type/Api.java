package pixlze.guildapi.net.type;

import pixlze.guildapi.GuildApi;
import pixlze.guildapi.net.event.NetEvents;

import java.util.List;

public abstract class Api {
    public final String name;
    private final List<Class<? extends Api>> dependencies;
    private boolean enabled = false;
    protected String baseURL;
    private int missingDeps;

    // TODO move api get posts here
    // TODO improve dependencies system to show which dependencies specifically are unloaded to ensure no duplications
    protected Api(String name, List<Class<? extends Api>> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
        missingDeps = dependencies.size();
        NetEvents.LOADED.register(this::onApiLoaded);
        NetEvents.DISABLED.register(this::onApiDisabled);
    }

    public boolean isDisabled() {
        return !enabled;
    }

    private void onApiLoaded(Api api) {
        GuildApi.LOGGER.info("{} says {} was loaded", name, api.name);
        if (this.depends(api)) dependencyLoaded();
    }

    private void onApiDisabled(Api api) {
        if (this.depends(api)) dependencyUnloaded();
    }

    public boolean depends(Api api) {
        return dependencies.contains(api.getClass());
    }

    private void dependencyLoaded() {
        --missingDeps;
        if (missingDeps == 0) {
            GuildApi.LOGGER.info("{} ready", name);
            ready();
        }
    }

    private void dependencyUnloaded() {
        ++missingDeps;
        unready();
    }

    protected void ready() {
        enable();
    }

    protected void unready() {
        disable();
    }

    public void enable() {
        if (!enabled) {
            GuildApi.LOGGER.info("enabling {}", name);
            enabled = true;
            NetEvents.LOADED.invoker().interact(this);
        }
    }

    public void disable() {
        if (enabled) {
            GuildApi.LOGGER.warn("disabling {} service", name);
            enabled = false;
            NetEvents.DISABLED.invoker().interact(this);
        }
    }

    public abstract void init();

    public abstract Api getInstance();
}
