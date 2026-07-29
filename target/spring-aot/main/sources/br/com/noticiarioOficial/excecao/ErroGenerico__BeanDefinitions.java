package br.com.noticiarioOficial.excecao;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ErroGenerico}.
 */
@Generated
public class ErroGenerico__BeanDefinitions {
  /**
   * Get the bean definition for 'erroGenerico'.
   */
  public static BeanDefinition getErroGenericoBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ErroGenerico.class);
    beanDefinition.setInstanceSupplier(ErroGenerico::new);
    return beanDefinition;
  }
}
