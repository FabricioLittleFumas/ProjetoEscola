package br.com.noticiarioOficial.service;

import br.com.noticiarioOficial.repository.AlunoRepository;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AlunoService}.
 */
@Generated
public class AlunoService__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'alunoService'.
   */
  private static BeanInstanceSupplier<AlunoService> getAlunoServiceInstanceSupplier() {
    return BeanInstanceSupplier.<AlunoService>forConstructor(AlunoRepository.class)
            .withGenerator((registeredBean, args) -> new AlunoService(args.get(0)));
  }

  /**
   * Get the bean definition for 'alunoService'.
   */
  public static BeanDefinition getAlunoServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AlunoService.class);
    beanDefinition.setInstanceSupplier(getAlunoServiceInstanceSupplier());
    return beanDefinition;
  }
}
