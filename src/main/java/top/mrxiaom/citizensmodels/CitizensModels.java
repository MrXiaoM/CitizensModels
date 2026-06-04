package top.mrxiaom.citizensmodels;
        
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.citizensmodels.api.IModelEngine;
import top.mrxiaom.citizensmodels.impl.bm.BetterModelV3;
import top.mrxiaom.citizensmodels.impl.meg.v3.ModelEngineV3;
import top.mrxiaom.citizensmodels.impl.meg.v4.ModelEngineV4;
import top.mrxiaom.citizensmodels.wrapper.ModelApiWrapper;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.func.LanguageManager;
import top.mrxiaom.pluginbase.resolver.DefaultLibraryResolver;
import top.mrxiaom.pluginbase.utils.ClassLoaderWrapper;
import top.mrxiaom.pluginbase.utils.ConfigUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

public class CitizensModels extends BukkitPlugin {
    public static CitizensModels getInstance() {
        return (CitizensModels) BukkitPlugin.getInstance();
    }

    public CitizensModels() throws Exception {
        super(options()
                .adventure(true)
                .scanIgnore("top.mrxiaom.citizensmodels.libs")
                .disableDefaultConfig(true)
        );

        try {
            //noinspection ResultOfMethodCallIgnored
            getDescription().getLibraries();
        } catch (LinkageError ignored) {
            info("正在检查依赖库状态");
            File librariesDir = ClassLoaderWrapper.isSupportLibraryLoader
                    ? new File("libraries")
                    : new File(this.getDataFolder(), "libraries");
            DefaultLibraryResolver resolver = new DefaultLibraryResolver(getLogger(), librariesDir);

            File overrideFile = resolve("./.override-libraries.yml");
            YamlConfiguration overrideLibraries = ConfigUtils.load(overrideFile);
            for (String key : overrideLibraries.getKeys(false)) {
                resolver.getStartsReplacer().put(key, overrideLibraries.getString(key));
            }
            resolver.addResolvedLibrary(BuildConstants.RESOLVED_LIBRARIES);

            List<URL> libraries = resolver.doResolve();
            info("正在添加 " + libraries.size() + " 个依赖库到类加载器");
            for (URL library : libraries) {
                this.classLoader.addURL(library);
            }
        }
    }
    private final ModelApiWrapper modelApi = new ModelApiWrapper();
    private IModelEngine modelEngine;
    private IModelEngine betterModel;

    @NotNull
    public IModelEngine getModelApi() {
        return modelApi;
    }

    @Nullable
    public IModelEngine getModelEngine() {
        return modelEngine;
    }

    @Nullable
    public IModelEngine getBetterModel() {
        return betterModel;
    }

    @Override
    @SuppressWarnings({"RedundantIfStatement"})
    protected boolean beforeEnableEarly() {
        boolean canEnable = false;

        if (initModelEngine()) canEnable = true;
        if (initBetterModel()) canEnable = true;

        if (canEnable) {
            modelApi.register(modelEngine);
            modelApi.register(betterModel);
            return true;
        }
        return false;
    }

    private boolean initModelEngine() {
        String megVersion = getModelEngineVersion();
        if (megVersion == null) {
            return false;
        }
        if (megVersion.startsWith("4.")) {
            modelEngine = new ModelEngineV4(getScheduler()::runTask);
            return true;
        }
        if (megVersion.startsWith("3.")) {
            modelEngine = new ModelEngineV3(getScheduler()::runTask);
            return true;
        }
        warn("当前 ModelEngine 版本 (" + megVersion + ") 不受支持!");
        return false;
    }

    private String getModelEngineVersion() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ModelEngine");
        if (plugin == null) return null;
        String ver = plugin.getDescription().getVersion();
        if (ver.startsWith("R")) {
            return ver.substring(1);
        }
        return ver;
    }

    private boolean initBetterModel() {
        String bmVersion = getBetterModelVersion();
        if (bmVersion == null) {
            return false;
        }
        if (bmVersion.startsWith("3.")) {
            betterModel = new BetterModelV3(getScheduler()::runTask);
            return true;
        }
        warn("当前 BetterModel 版本 (" + bmVersion + ") 不受支持!");
        return false;
    }

    private String getBetterModelVersion() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("BetterModel");
        if (plugin == null) return null;
        return plugin.getDescription().getVersion();
    }

    @Override
    protected void beforeEnable() {
        LanguageManager.inst()
                .setLangFile("messages.yml")
                .register(Messages.class, Messages::holder);
    }

    @Override
    protected void afterEnable() {
        getLogger().info("CitizensModels 加载完毕");
    }

    @Override
    protected void afterDisable() {
        for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
            Entity entity = npc.getEntity();
            if (entity != null) {
                Location loc = entity.getLocation();
                if (modelApi.destroy(entity)) {
                    npc.despawn();
                    npc.spawn(loc);
                }
            }
        }
    }
}
