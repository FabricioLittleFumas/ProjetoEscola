package br.com.noticiarioOficial.controller;

import br.com.noticiarioOficial.service.AlunoService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AlunoController}.
 */
@Generated
public class AlunoController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'alunoController'.
   */
  private static BeanInstanceSupplier<AlunoController> getAlunoControllerInstanceSupplier() {
    return BeanInstanceSupplier.<AlunoController>forConstructor(AlunoService.class)
            .withGenerator((registeredBean, args) -> new AlunoController(args.get(0)));
  }

  /**
   * Get the bean definition for 'alunoController'.
   */
  public static BeanDefinition getAlunoControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AlunoController.class);
    beanDefinition.setInstanceSupplier(getAlunoControllerInstanceSupplier());
    return beanDefinition;
  }
}
