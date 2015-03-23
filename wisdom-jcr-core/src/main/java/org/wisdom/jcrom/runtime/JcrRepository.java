/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2015 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.wisdom.jcrom.runtime;

import org.jcrom.Jcrom;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.model.Crud;
import org.wisdom.api.model.Repository;
import org.wisdom.jcrom.conf.JcromConfiguration;
import org.wisdom.jcrom.object.JcrCrud;

import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;

/**
 * Created by antoine on 14/07/2014.
 */
public class JcrRepository implements Repository<javax.jcr.Repository> {

    private Logger logger = LoggerFactory.getLogger(JcromCrudProvider.class);

    private final javax.jcr.Repository repository;

    private final JcromConfiguration jcromConfiguration;

    private final Jcrom jcrom;

    private final Session session;

    private Collection<ServiceRegistration> registrations = new ArrayList<>();

    private Collection<JcrCrud<?, ?>> crudServices = new ArrayList<>();

    public JcrRepository(JcromConfiguration jcromConfiguration, RepositoryFactory repositoryFactory, ApplicationConfiguration applicationConfiguration) throws RepositoryException {
        Thread.currentThread().setContextClassLoader(repositoryFactory.getClass().getClassLoader());
        logger.info("Loading JCR repository " + jcromConfiguration.getRepository());
        this.repository = repositoryFactory.getRepository(applicationConfiguration.getConfiguration("jcr").getConfiguration(jcromConfiguration.getRepository()).asMap());
        this.jcrom = new Jcrom(jcromConfiguration.isCleanNames(), jcromConfiguration.isDynamicInstantiation());
        this.jcromConfiguration = jcromConfiguration;
        Thread.currentThread().setContextClassLoader(JcrRepository.class.getClassLoader());
        this.session = repository.login();
    }

    protected void addCrudService(Class entity, BundleContext bundleContext) throws RepositoryException {
        jcrom.map(entity);
        JcrCrudService<? extends Object> jcromCrudService;
        jcromCrudService = new JcrCrudService<Object>(this, entity);
        crudServices.add(jcromCrudService);
        registerCrud(bundleContext, jcromCrudService);
    }

    protected void registerCrud(BundleContext context, JcrCrudService crud) {
        Dictionary prop = jcromConfiguration.toDictionary();
        prop.put(Crud.ENTITY_CLASS_PROPERTY, crud.getEntityClass());
        prop.put(Crud.ENTITY_CLASSNAME_PROPERTY, crud.getEntityClass().getName());
        registrations.add(context.registerService(new String[]{Crud.class.getName(), JcrCrud.class.getName(), crud.getClass().getName()}, crud, prop));
    }

    protected void destroy() {
        for (ServiceRegistration reg : registrations) {
            reg.unregister();
        }
        registrations.clear();
        crudServices.clear();
        session.logout();
    }

    public javax.jcr.Repository getRepository() {
        return repository;
    }

    public Jcrom getJcrom() {
        return jcrom;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public Collection<Crud<?, ?>> getCrudServices() {
        return (Collection) crudServices;
    }


    @Override
    public String getName() {
        return jcromConfiguration.getRepository();
    }

    @Override
    public String getType() {
        return "jcr-repository";
    }

    @Override
    public Class<javax.jcr.Repository> getRepositoryClass() {
        return javax.jcr.Repository.class;
    }

    @Override
    public javax.jcr.Repository get() {
        return repository;
    }
}
