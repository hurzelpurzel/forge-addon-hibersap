package org.addon.hibersap.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.hibersap.configuration.xml.SessionManagerConfig;
import org.hibersap.generator.manager.ConnectionPropertiesManager;
import org.hibersap.generator.sap.SAPFunctionModuleSearch;
import org.hibersap.generator.util.FilterCollection;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

import javax.inject.Inject;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * See : https://github.com/shillner/maven-cdi-plugin-utils
 */
public abstract class AbstractSAPMojo<shell> extends AbstractMojo {


    /**
     * The SAP connection properties *
     */
    @Inject
    protected ConnectionPropertiesManager sapConnectionPropertiesManager;

    protected PrintStream shell = System.out;


    /**
     * Creates the necessary session manager configuration for the function module search
     *
     * @return the session manager configuration
     */
    protected SessionManagerConfig createSessionManagerConfig() {
        final SessionManagerConfig sessionManagerConfig = new SessionManagerConfig();

        sessionManagerConfig.setName(this.sapConnectionPropertiesManager.getSAPProperty("session-manager.name"));
        // Setting JCo context is not necessary, because it's set by default when creating a new SessionManangerConfig object
        sessionManagerConfig.addAnnotatedClass(SAPFunctionModuleSearch.class);

        //Filter JCo properties from property list
        //New Set necessary, because the sapConnection properties shall not be affected
        final Set<Map.Entry<Object, Object>> jcoConnectionProperties = new HashSet<Map.Entry<Object, Object>>(
                this.sapConnectionPropertiesManager.getAllSAPProperties());
        final FilterCollection filter = new FilterCollection(jcoConnectionProperties, "jco", "context");

        filter.filter();

        for (final Map.Entry<Object, Object> entry : jcoConnectionProperties) {
            sessionManagerConfig.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }

        return sessionManagerConfig;
    }
}


