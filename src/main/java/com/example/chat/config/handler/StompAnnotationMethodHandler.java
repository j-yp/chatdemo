package com.example.chat.config.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import com.example.chat.config.resource.StompUserResource;
import com.example.chat.config.utils.PackageUtil;

@Configuration
public class StompAnnotationMethodHandler implements ApplicationContextAware, InitializingBean{
	private ApplicationContext applicationContext;
	
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private StompUserResource stompUserResource;
	
	@Override
	public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Nullable
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	private List<Class<?>> annoTypes;
	
	public List<Class<?>> getAnnoTypes(){
		if(this.annoTypes == null) {
			this.setAnnoTypes(this.getAnnoTationPackageClasses());
		}
		return annoTypes;
	}
	
	public void setAnnoTypes(List<Class<?>> annoTypes) {
		this.annoTypes = annoTypes;
	}

	public List<Class<?>> getAnnoTationPackageClasses(){
        List<String> annoPackages = this.stompUserResource.getAnnoPackages();
        List<Class<?>> list = this.getClassesFromPackages(annoPackages);
        return list.stream().filter(clazz -> {
        	if(clazz.isAnnotation() && clazz.getName().endsWith("Controller")) {
        		return true;
        	}
        	return false;
        }).collect(Collectors.toList());
    }
	
	public List<Class<?>> getClassesFromPackages(List<String> packages){
		List<Class<?>> list = packages.stream().collect(
				Collectors.reducing(new ArrayList<Class<?>>(), 
				packageName -> {
					try {
						return PackageUtil.getClassName(packageName).stream().map(className -> {
							try {
								return Class.forName(className);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							return null;
						}).collect(Collectors.toList());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}, 
				(classes1, classes2) -> {classes1.addAll(classes2); return classes1;}));
		return list;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ApplicationContext context = this.getApplicationContext();
		if (context == null) {
			return;
		}
		/*for (String beanName : context.getBeanNamesForType(Object.class)) {
			if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
				Class<?> beanType = null;
				try {
					beanType = context.getType(beanName);
				}
				catch (Throwable ex) {
					// An unresolvable bean type, probably from a lazy bean - let's ignore it.
					if (logger.isDebugEnabled()) {
						logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
					}
				}
				if (beanType != null && isHandler(beanType)) {
					detectHandlerMethods(beanName);
				}
			}
		}*/
	}
	
}
