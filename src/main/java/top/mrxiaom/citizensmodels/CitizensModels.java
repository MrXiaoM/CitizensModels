package top.mrxiaom.citizensmodels;
        
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import top.mrxiaom.citizensmodels.api.IModelEngine;
import top.mrxiaom.citizensmodels.meg.v3.ModelEngineV3;
import top.mrxiaom.citizensmodels.meg.v4.ModelEngineV4;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.func.LanguageManager;
import top.mrxiaom.pluginbase.resolver.DefaultLibraryResolver;
import top.mrxiaom.pluginbase.utils.ClassLoaderWrapper;

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

        info("正在检查依赖库状态");
        File librariesDir = ClassLoaderWrapper.isSupportLibraryLoader
                ? new File("libraries")
                : new File(this.getDataFolder(), "libraries");
        DefaultLibraryResolver resolver = new DefaultLibraryResolver(getLogger(), librariesDir);

        resolver.addResolvedLibrary(BuildConstants.RESOLVED_LIBRARIES);

        List<URL> libraries = resolver.doResolve();
        info("正在添加 " + libraries.size() + " 个依赖库到类加载器");
        for (URL library : libraries) {
            this.classLoader.addURL(library);
        }
    }
    private IModelEngine modelEngine;
    public IModelEngine getModelEngine() {
        return modelEngine;
    }

    @Override
    @SuppressWarnings({"deprecation"})
    public void onEnable() {
        if (initModelEngine()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        super.onEnable();
    }

    private boolean initModelEngine() {
        String megVersion = getModelEngineVersion();
        if (megVersion.startsWith("4.")) {
            modelEngine = new ModelEngineV4(getScheduler()::runTask);
            return false;
        }
        if (megVersion.startsWith("3.")) {
            modelEngine = new ModelEngineV3(getScheduler()::runTask);
            return false;
        }
        warn("当前 ModelEngine 版本 (" + megVersion + ") 不受支持!");
        return true;
    }

    private String getModelEngineVersion() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ModelEngine");
        if (plugin == null) return "unknown";
        String ver = plugin.getDescription().getVersion();
        if (ver.startsWith("R")) {
            return ver.substring(1);
        }
        return ver;
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
        if (modelEngine != null) {
            modelEngine.onDisable();
            modelEngine = null;
        }
    }
}
