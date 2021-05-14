package org.camunda.bpm.scenario.cucumber;

import io.cucumber.java.en.When;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.impl.test.TestHelper;
import org.camunda.bpm.engine.test.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Martin Schimak
 */
public class CamundaCucumberPlugin implements ConcurrentEventListener {

  protected static final Logger log = LoggerFactory.getLogger(CamundaCucumberPlugin.class.getPackage().getName());
  protected static final String configurationResource = "camunda.cfg.xml";
  protected static final Pattern pattern = Pattern.compile("(.+)\\.(.+)\\((.*)\\).*");

  protected Method testMethod;
  protected String deploymentId;

  @Override
  public void setEventPublisher(EventPublisher eventPublisher) {
    eventPublisher.registerHandlerFor(TestCaseStarted.class, testCaseStartedEventHandler);
    eventPublisher.registerHandlerFor(TestCaseFinished.class, testCaseFinishedEventHandler);
  }

  protected final EventHandler<TestCaseStarted> testCaseStartedEventHandler = event -> deploy(event);
  protected final EventHandler<TestCaseFinished> testCaseFinishedEventHandler = event -> undeploy(event);

  protected void deploy(TestCaseStarted event) {
    testMethod = when(event);
    Deployment deployment = deploymentAnnotation(testMethod);
    if (deployment != null) {
      deploymentId = TestHelper.annotationDeploymentSetUp(
        getProcessEngine(),
        testMethod.getDeclaringClass(),
        event.getTestCase().getName(),
        deployment
      );
    }
  }

  protected void undeploy(TestCaseFinished event) {
    TestHelper.annotationDeploymentTearDown(
      getProcessEngine(),
      deploymentId,
      testMethod.getDeclaringClass(),
      event.getTestCase().getName()
    );
  }

  protected Deployment deploymentAnnotation(Method method) {
    Deployment annotation = null;
    if (method != null) {
      annotation = method.getAnnotation(Deployment.class);
      if (annotation == null)
        annotation = method.getDeclaringClass().getAnnotation(Deployment.class);
    }
    return annotation;
  }

  protected Method when(TestCaseStarted event) {
    List<TestStep> testSteps = event.getTestCase().getTestSteps();
    return testSteps.stream()
      .map(s -> method(s))
      .filter(m -> isWhen(m))
      .findFirst()
      .orElse(null);
  }

  protected boolean isWhen(Method method) {
    return method != null &&
      (method.getAnnotation(When.class) != null
        || method.getAnnotation(When.Whens.class) != null
        || method.getAnnotation(io.cucumber.java.af.Wanneer.class) != null
        || method.getAnnotation(io.cucumber.java.af.Wanneer.Wanneers.class) != null
        || method.getAnnotation(io.cucumber.java.ar.متى.class) != null
        || method.getAnnotation(io.cucumber.java.ar.متى.متىs.class) != null
        || method.getAnnotation(io.cucumber.java.ar.عندما.class) != null
        || method.getAnnotation(io.cucumber.java.ar.عندما.عندماs.class) != null
        || method.getAnnotation(io.cucumber.java.an.Cuan.class) != null
        || method.getAnnotation(io.cucumber.java.an.Cuan.Cuans.class) != null
        || method.getAnnotation(io.cucumber.java.am.Եթե.class) != null
        || method.getAnnotation(io.cucumber.java.am.Եթե.Եթեs.class) != null
        || method.getAnnotation(io.cucumber.java.am.Երբ.class) != null
        || method.getAnnotation(io.cucumber.java.am.Երբ.Երբs.class) != null
        || method.getAnnotation(io.cucumber.java.ast.Cuando.class) != null
        || method.getAnnotation(io.cucumber.java.ast.Cuando.Cuandos.class) != null
        || method.getAnnotation(io.cucumber.java.en_au.Itsjustunbelievable.class) != null
        || method.getAnnotation(io.cucumber.java.en_au.Itsjustunbelievable.Itsjustunbelievables.class) != null
        || method.getAnnotation(io.cucumber.java.az.Əgər.class) != null
        || method.getAnnotation(io.cucumber.java.az.Əgər.Əgərs.class) != null
        || method.getAnnotation(io.cucumber.java.az.Nəvaxtki.class) != null
        || method.getAnnotation(io.cucumber.java.az.Nəvaxtki.Nəvaxtkis.class) != null
        || method.getAnnotation(io.cucumber.java.bs.Kada.class) != null
        || method.getAnnotation(io.cucumber.java.bs.Kada.Kadas.class) != null
        || method.getAnnotation(io.cucumber.java.bg.Когато.class) != null
        || method.getAnnotation(io.cucumber.java.bg.Когато.Когатоs.class) != null
        || method.getAnnotation(io.cucumber.java.ca.Quan.class) != null
        || method.getAnnotation(io.cucumber.java.ca.Quan.Quans.class) != null
        || method.getAnnotation(io.cucumber.java.zh_cn.当.class) != null
        || method.getAnnotation(io.cucumber.java.zh_cn.当.当s.class) != null
        || method.getAnnotation(io.cucumber.java.zh_tw.當.class) != null
        || method.getAnnotation(io.cucumber.java.zh_tw.當.當s.class) != null
        || method.getAnnotation(io.cucumber.java.ht.Lè.class) != null
        || method.getAnnotation(io.cucumber.java.ht.Lè.Lès.class) != null
        || method.getAnnotation(io.cucumber.java.ht.Le.class) != null
        || method.getAnnotation(io.cucumber.java.ht.Le.Les.class) != null
        || method.getAnnotation(io.cucumber.java.hr.Kada.class) != null
        || method.getAnnotation(io.cucumber.java.hr.Kada.Kadas.class) != null
        || method.getAnnotation(io.cucumber.java.hr.Kad.class) != null
        || method.getAnnotation(io.cucumber.java.hr.Kad.Kads.class) != null
        || method.getAnnotation(io.cucumber.java.cs.Když.class) != null
        || method.getAnnotation(io.cucumber.java.cs.Když.Kdyžs.class) != null
        || method.getAnnotation(io.cucumber.java.da.Når.class) != null
        || method.getAnnotation(io.cucumber.java.da.Når.Nårs.class) != null
        || method.getAnnotation(io.cucumber.java.nl.Als.class) != null
        || method.getAnnotation(io.cucumber.java.nl.Als.Alss.class) != null
        || method.getAnnotation(io.cucumber.java.nl.Wanneer.class) != null
        || method.getAnnotation(io.cucumber.java.nl.Wanneer.Wanneers.class) != null
        || method.getAnnotation(io.cucumber.java.eo.Se.class) != null
        || method.getAnnotation(io.cucumber.java.eo.Se.Ses.class) != null
        || method.getAnnotation(io.cucumber.java.et.Kui.class) != null
        || method.getAnnotation(io.cucumber.java.et.Kui.Kuis.class) != null
        || method.getAnnotation(io.cucumber.java.fi.Kun.class) != null
        || method.getAnnotation(io.cucumber.java.fi.Kun.Kuns.class) != null
        || method.getAnnotation(io.cucumber.java.fr.Quand.class) != null
        || method.getAnnotation(io.cucumber.java.fr.Quand.Quands.class) != null
        || method.getAnnotation(io.cucumber.java.fr.Lorsque.class) != null
        || method.getAnnotation(io.cucumber.java.fr.Lorsque.Lorsques.class) != null
        || method.getAnnotation(io.cucumber.java.fr.Lorsqu.class) != null
        || method.getAnnotation(io.cucumber.java.fr.Lorsqu.Lorsqus.class) != null
        || method.getAnnotation(io.cucumber.java.gl.Cando.class) != null
        || method.getAnnotation(io.cucumber.java.gl.Cando.Candos.class) != null
        || method.getAnnotation(io.cucumber.java.ka.როდესაც.class) != null
        || method.getAnnotation(io.cucumber.java.ka.როდესაც.როდესაცs.class) != null
        || method.getAnnotation(io.cucumber.java.de.Wenn.class) != null
        || method.getAnnotation(io.cucumber.java.de.Wenn.Wenns.class) != null
        || method.getAnnotation(io.cucumber.java.el.Όταν.class) != null
        || method.getAnnotation(io.cucumber.java.el.Όταν.Ότανs.class) != null
        || method.getAnnotation(io.cucumber.java.gj.ક્યારે.class) != null
        || method.getAnnotation(io.cucumber.java.gj.ક્યારે.ક્યારેs.class) != null
        || method.getAnnotation(io.cucumber.java.he.כאשר.class) != null
        || method.getAnnotation(io.cucumber.java.he.כאשר.כאשרs.class) != null
        || method.getAnnotation(io.cucumber.java.hi.जब.class) != null
        || method.getAnnotation(io.cucumber.java.hi.जब.जबs.class) != null
        || method.getAnnotation(io.cucumber.java.hi.कदा.class) != null
        || method.getAnnotation(io.cucumber.java.hi.कदा.कदाs.class) != null
        || method.getAnnotation(io.cucumber.java.hu.Majd.class) != null
        || method.getAnnotation(io.cucumber.java.hu.Majd.Majds.class) != null
        || method.getAnnotation(io.cucumber.java.hu.Ha.class) != null
        || method.getAnnotation(io.cucumber.java.hu.Ha.Has.class) != null
        || method.getAnnotation(io.cucumber.java.hu.Amikor.class) != null
        || method.getAnnotation(io.cucumber.java.hu.Amikor.Amikors.class) != null
        || method.getAnnotation(io.cucumber.java.is.Þegar.class) != null
        || method.getAnnotation(io.cucumber.java.is.Þegar.Þegars.class) != null
        || method.getAnnotation(io.cucumber.java.id.Ketika.class) != null
        || method.getAnnotation(io.cucumber.java.id.Ketika.Ketikas.class) != null
        || method.getAnnotation(io.cucumber.java.ga.Nuaira.class) != null
        || method.getAnnotation(io.cucumber.java.ga.Nuaira.Nuairas.class) != null
        || method.getAnnotation(io.cucumber.java.ga.Nuairba.class) != null
        || method.getAnnotation(io.cucumber.java.ga.Nuairba.Nuairbas.class) != null
        || method.getAnnotation(io.cucumber.java.ga.Nuairnach.class) != null
        || method.getAnnotation(io.cucumber.java.ga.Nuairnach.Nuairnachs.class) != null
        || method.getAnnotation(io.cucumber.java.ga.Nuairnár.class) != null
        || method.getAnnotation(io.cucumber.java.ga.Nuairnár.Nuairnárs.class) != null
        || method.getAnnotation(io.cucumber.java.it.Quando.class) != null
        || method.getAnnotation(io.cucumber.java.it.Quando.Quandos.class) != null
        || method.getAnnotation(io.cucumber.java.ja.もし.class) != null
        || method.getAnnotation(io.cucumber.java.ja.もし.もしs.class) != null
        || method.getAnnotation(io.cucumber.java.jv.Manawa.class) != null
        || method.getAnnotation(io.cucumber.java.jv.Manawa.Manawas.class) != null
        || method.getAnnotation(io.cucumber.java.jv.Menawa.class) != null
        || method.getAnnotation(io.cucumber.java.jv.Menawa.Menawas.class) != null
        || method.getAnnotation(io.cucumber.java.kn.ಸ್ಥಿತಿಯನ್ನು.class) != null
        || method.getAnnotation(io.cucumber.java.kn.ಸ್ಥಿತಿಯನ್ನು.ಸ್ಥಿತಿಯನ್ನುs.class) != null
        || method.getAnnotation(io.cucumber.java.tlh.qaSDI.class) != null
        || method.getAnnotation(io.cucumber.java.tlh.qaSDI.qaSDIs.class) != null
        || method.getAnnotation(io.cucumber.java.ko.만일.class) != null
        || method.getAnnotation(io.cucumber.java.ko.만일.만일s.class) != null
        || method.getAnnotation(io.cucumber.java.ko.만약.class) != null
        || method.getAnnotation(io.cucumber.java.ko.만약.만약s.class) != null
        || method.getAnnotation(io.cucumber.java.en_lol.WEN.class) != null
        || method.getAnnotation(io.cucumber.java.en_lol.WEN.WENs.class) != null
        || method.getAnnotation(io.cucumber.java.lv.Ja.class) != null
        || method.getAnnotation(io.cucumber.java.lv.Ja.Jas.class) != null
        || method.getAnnotation(io.cucumber.java.lt.Kai.class) != null
        || method.getAnnotation(io.cucumber.java.lt.Kai.Kais.class) != null
        || method.getAnnotation(io.cucumber.java.lu.wann.class) != null
        || method.getAnnotation(io.cucumber.java.lu.wann.wanns.class) != null
        || method.getAnnotation(io.cucumber.java.mk_cyrl.Кога.class) != null
        || method.getAnnotation(io.cucumber.java.mk_cyrl.Кога.Когаs.class) != null
        || method.getAnnotation(io.cucumber.java.mk_latn.Koga.class) != null
        || method.getAnnotation(io.cucumber.java.mk_latn.Koga.Kogas.class) != null
        || method.getAnnotation(io.cucumber.java.bm.Apabila.class) != null
        || method.getAnnotation(io.cucumber.java.bm.Apabila.Apabilas.class) != null
        || method.getAnnotation(io.cucumber.java.mr.जेव्हा.class) != null
        || method.getAnnotation(io.cucumber.java.mr.जेव्हा.जेव्हाs.class) != null
        || method.getAnnotation(io.cucumber.java.mn.Хэрэв.class) != null
        || method.getAnnotation(io.cucumber.java.mn.Хэрэв.Хэрэвs.class) != null
        || method.getAnnotation(io.cucumber.java.ne.जब.class) != null
        || method.getAnnotation(io.cucumber.java.ne.जब.जबs.class) != null
        || method.getAnnotation(io.cucumber.java.no.Når.class) != null
        || method.getAnnotation(io.cucumber.java.no.Når.Nårs.class) != null
        || method.getAnnotation(io.cucumber.java.en_old.Tha.class) != null
        || method.getAnnotation(io.cucumber.java.en_old.Tha.Thas.class) != null
        || method.getAnnotation(io.cucumber.java.en_old.Þa.class) != null
        || method.getAnnotation(io.cucumber.java.en_old.Þa.Þas.class) != null
        || method.getAnnotation(io.cucumber.java.en_old.Ða.class) != null
        || method.getAnnotation(io.cucumber.java.en_old.Ða.Ðas.class) != null
        || method.getAnnotation(io.cucumber.java.pa.ਜਦੋਂ.class) != null
        || method.getAnnotation(io.cucumber.java.pa.ਜਦੋਂ.ਜਦੋਂs.class) != null
        || method.getAnnotation(io.cucumber.java.fa.هنگامی.class) != null
        || method.getAnnotation(io.cucumber.java.fa.هنگامی.هنگامیs.class) != null
        || method.getAnnotation(io.cucumber.java.en_pirate.Blimey.class) != null
        || method.getAnnotation(io.cucumber.java.en_pirate.Blimey.Blimeys.class) != null
        || method.getAnnotation(io.cucumber.java.pl.Jeżeli.class) != null
        || method.getAnnotation(io.cucumber.java.pl.Jeżeli.Jeżelis.class) != null
        || method.getAnnotation(io.cucumber.java.pl.Jeśli.class) != null
        || method.getAnnotation(io.cucumber.java.pl.Jeśli.Jeślis.class) != null
        || method.getAnnotation(io.cucumber.java.pl.Gdy.class) != null
        || method.getAnnotation(io.cucumber.java.pl.Gdy.Gdys.class) != null
        || method.getAnnotation(io.cucumber.java.pl.Kiedy.class) != null
        || method.getAnnotation(io.cucumber.java.pl.Kiedy.Kiedys.class) != null
        || method.getAnnotation(io.cucumber.java.pt.Quando.class) != null
        || method.getAnnotation(io.cucumber.java.pt.Quando.Quandos.class) != null
        || method.getAnnotation(io.cucumber.java.ro.Cand.class) != null
        || method.getAnnotation(io.cucumber.java.ro.Cand.Cands.class) != null
        || method.getAnnotation(io.cucumber.java.ro.Când.class) != null
        || method.getAnnotation(io.cucumber.java.ro.Când.Cânds.class) != null
        || method.getAnnotation(io.cucumber.java.ru.Когда.class) != null
        || method.getAnnotation(io.cucumber.java.ru.Когда.Когдаs.class) != null
        || method.getAnnotation(io.cucumber.java.ru.Если.class) != null
        || method.getAnnotation(io.cucumber.java.ru.Если.Еслиs.class) != null
        || method.getAnnotation(io.cucumber.java.en_scouse.Wun.class) != null
        || method.getAnnotation(io.cucumber.java.en_scouse.Wun.Wuns.class) != null
        || method.getAnnotation(io.cucumber.java.en_scouse.Youseknowlikewhen.class) != null
        || method.getAnnotation(io.cucumber.java.en_scouse.Youseknowlikewhen.Youseknowlikewhens.class) != null
        || method.getAnnotation(io.cucumber.java.sr_cyrl.Када.class) != null
        || method.getAnnotation(io.cucumber.java.sr_cyrl.Када.Кадаs.class) != null
        || method.getAnnotation(io.cucumber.java.sr_cyrl.Кад.class) != null
        || method.getAnnotation(io.cucumber.java.sr_cyrl.Кад.Кадs.class) != null
        || method.getAnnotation(io.cucumber.java.sr_latn.Kada.class) != null
        || method.getAnnotation(io.cucumber.java.sr_latn.Kada.Kadas.class) != null
        || method.getAnnotation(io.cucumber.java.sr_latn.Kad.class) != null
        || method.getAnnotation(io.cucumber.java.sr_latn.Kad.Kads.class) != null
        || method.getAnnotation(io.cucumber.java.sk.Keď.class) != null
        || method.getAnnotation(io.cucumber.java.sk.Keď.Keďs.class) != null
        || method.getAnnotation(io.cucumber.java.sk.Ak.class) != null
        || method.getAnnotation(io.cucumber.java.sk.Ak.Aks.class) != null
        || method.getAnnotation(io.cucumber.java.sl.Ko.class) != null
        || method.getAnnotation(io.cucumber.java.sl.Ko.Kos.class) != null
        || method.getAnnotation(io.cucumber.java.sl.Ce.class) != null
        || method.getAnnotation(io.cucumber.java.sl.Ce.Ces.class) != null
        || method.getAnnotation(io.cucumber.java.sl.Če.class) != null
        || method.getAnnotation(io.cucumber.java.sl.Če.Čes.class) != null
        || method.getAnnotation(io.cucumber.java.sl.Kadar.class) != null
        || method.getAnnotation(io.cucumber.java.sl.Kadar.Kadars.class) != null
        || method.getAnnotation(io.cucumber.java.es.Cuando.class) != null
        || method.getAnnotation(io.cucumber.java.es.Cuando.Cuandos.class) != null
        || method.getAnnotation(io.cucumber.java.sv.När.class) != null
        || method.getAnnotation(io.cucumber.java.sv.När.Närs.class) != null
        || method.getAnnotation(io.cucumber.java.ta.எப்போது.class) != null
        || method.getAnnotation(io.cucumber.java.ta.எப்போது.எப்போதுs.class) != null
        || method.getAnnotation(io.cucumber.java.tt.Әгәр.class) != null
        || method.getAnnotation(io.cucumber.java.tt.Әгәр.Әгәрs.class) != null
        || method.getAnnotation(io.cucumber.java.te.ఈపరిస్థితిలో.class) != null
        || method.getAnnotation(io.cucumber.java.te.ఈపరిస్థితిలో.ఈపరిస్థితిలోs.class) != null
        || method.getAnnotation(io.cucumber.java.th.เมื่อ.class) != null
        || method.getAnnotation(io.cucumber.java.th.เมื่อ.เมื่อs.class) != null
        || method.getAnnotation(io.cucumber.java.tr.Eğerki.class) != null
        || method.getAnnotation(io.cucumber.java.tr.Eğerki.Eğerkis.class) != null
        || method.getAnnotation(io.cucumber.java.uk.Якщо.class) != null
        || method.getAnnotation(io.cucumber.java.uk.Якщо.Якщоs.class) != null
        || method.getAnnotation(io.cucumber.java.uk.Коли.class) != null
        || method.getAnnotation(io.cucumber.java.uk.Коли.Колиs.class) != null
        || method.getAnnotation(io.cucumber.java.ur.جب.class) != null
        || method.getAnnotation(io.cucumber.java.ur.جب.جبs.class) != null
        || method.getAnnotation(io.cucumber.java.uz.Агар.class) != null
        || method.getAnnotation(io.cucumber.java.uz.Агар.Агарs.class) != null
        || method.getAnnotation(io.cucumber.java.vi.Khi.class) != null
        || method.getAnnotation(io.cucumber.java.vi.Khi.Khis.class) != null
        || method.getAnnotation(io.cucumber.java.cy_gb.Pryd.class) != null
        || method.getAnnotation(io.cucumber.java.cy_gb.Pryd.Pryds.class) != null
      );
  }

  protected Method method(TestStep testStep) {
    try {
      Matcher matcher = pattern.matcher(testStep.getCodeLocation());
      if (matcher.matches()) {
        String className = matcher.group(1);
        String methodName = matcher.group(2);
        String[] parameterTypeNames = matcher.group(3).split(",");
        boolean hasParameters = !(parameterTypeNames.length == 1 && parameterTypeNames[0].length() == 0);
        List<Class<?>> parameterTypes = hasParameters ? Arrays.stream(parameterTypeNames).map(name -> {
          try {
            return (Class<?>) Class.forName(name);
          } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        }).collect(Collectors.toList()) : new ArrayList<>();
        Class<?> cls = Class.forName(className);
        return cls.getDeclaredMethod(methodName, parameterTypes.toArray(new Class[0]));
      }
    } catch (Throwable exception) {
      log.error(String.format("Cucumber code location '%s' could not be transformed to JVM method.", testStep.getCodeLocation()), exception);
    }
    return null;
  }

  public static ProcessEngine getProcessEngine() {
    return TestHelper.getProcessEngine(configurationResource);
  }

  public static RuntimeService getRuntimeService() {
    return getProcessEngine().getRuntimeService();
  }

  public static RepositoryService getRepositoryService() {
    return getProcessEngine().getRepositoryService();
  }

  public static FormService getFormService() {
    return getProcessEngine().getFormService();
  }

  public static TaskService getTaskService() {
    return getProcessEngine().getTaskService();
  }

  public static HistoryService getHistoryService() {
    return getProcessEngine().getHistoryService();
  }

  public static IdentityService getIdentityService() {
    return getProcessEngine().getIdentityService();
  }

  public static ManagementService getManagementService() {
    return getProcessEngine().getManagementService();
  }

  public static AuthorizationService getAuthorizationService() {
    return getProcessEngine().getAuthorizationService();
  }

  public static CaseService getCaseService() {
    return getProcessEngine().getCaseService();
  }

  public static FilterService getFilterService() {
    return getProcessEngine().getFilterService();
  }

  public static ExternalTaskService getExternalTaskService() {
    return getProcessEngine().getExternalTaskService();
  }

  public static DecisionService getDecisionService() {
    return getProcessEngine().getDecisionService();
  }

}
