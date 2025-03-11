package scope.soot;

import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.TargetClassPath;
import soot.EntryPoints;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.io.File;
import java.util.List;

public class SootSetup {

    private SootClass targetClass;

    public void setupSoot(String targetClassName) {
        G.reset();

        Options.v().set_whole_program(true);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_keep_line_number(true);

        Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + TargetClassPath.TARGET_CLASS_PATH);

        Options.v().setPhaseOption("jb.sils", "enabled:false");
        Options.v().setPhaseOption("jb", "use-original-names:true");

        Options.v().set_include(List.of(targetClassName));
        Options.v().setPhaseOption("cg.cha", "on");
        Options.v().setPhaseOption("cg.cha", "all-reachable:true");

        targetClass = Scene.v().forceResolve(targetClassName, SootClass.BODIES);
        targetClass.setApplicationClass();

        Scene.v().loadNecessaryClasses();
        Scene.v().setEntryPoints(EntryPoints.v().application());

        PackManager.v().getPack("cg").apply();
    }

    public SootMethod resolveMethod(MethodSignature methodSignature) {
        String signature =
                methodSignature.getReturnType()
                        + " "
                        + methodSignature.getMethodName()
                        + "("
                        + String.join(",", methodSignature.getParameters())
                        + ")";

        return targetClass.getMethod(signature);
    }
}
