How to setup portal to use shared datasources for all portal containers
=======================================================================

Normally you need to setup 2 datasources (IDM and JCR) for each portal container. So if you have 10 portal containers,
you need to add 20 datasources into standalone.xml . Bad thing is that you also can't share same database if you want data
for each portal container isolated from each other.

Here are the steps to setup portal to use just 2 shared datasources (both can run on one DB) for all portal containers.

1) In GATEIN_HOME/standalone/configuration/standalone.xml you will need just 2 datasources for portal (one for IDM and one for JCR).
But IDM datasource needs to be renamed to "java:/jdbcidm" and JCR datasource to "java:/jdbcjcr". Renaming is needed because
datasources will be shared among all portal containers (Note that you still need to define separate security domain for each portal
container. So for 10 portal containers, you need to have 10 security domains)

2) Setup IDM. In GATEIN_HOME/gatein/gatein.ear/portal.war/WEB-INF/conf/organization/idm-configuration.xml you need to change this:

2.a) Delete suffix ${container.name.suffix} from property "hibernate.connection.datasource" of HibernateService. So the property
value will look like this:
<property name="hibernate.connection.datasource" value="${gatein.idm.datasource.name}"/>

2.b) Remove ${container.name.suffix} from property "bind-name" of InitialContextInitializer plugin. So the propery will look like this:
        <value-param>
          <name>bind-name</name>
          <value>${gatein.idm.datasource.name}</value>
        </value-param>

3) Setup JCR.

3.a) In GATEIN_HOME/gatein/gatein.ear/portal.war/WEB-INF/conf/jcr/jcr-configuration.xml you also need to remove ${container.name.suffix} from
property "bind-name" of InitialContextInitializer plugin similarly like for IDM in previous step.

3.b) In GATEIN_HOME/gatein/gatein.ear/portal.war/WEB-INF/conf/jcr/repository-configuration.xml there are 2 changes for every workspace (So 6 changes in total in this file).
So for each workspace the mentioned 2 changes are:
- Remove ${container.name.suffix} from property "source-name" of container configuration. So the property value will look like this:
<property name="source-name" value="${gatein.jcr.datasource.name}"/>

- Remove ${container.name.suffix} from property "jbosscache-cl-cache.jdbc.datasource" of lock-manager. So the property value will look like this:
              <property name="jbosscache-cl-cache.jdbc.datasource"
                        value="${gatein.jcr.datasource.name}"/>

Don't forget to repeat these 2 steps for each workspace

3.c) In same file you need to add/change property "db-tablename-suffix" of container configuration for each workspace and here you need to _add_
the ${container.name.suffix} part. It is needed for having separate set of tables for each combination of workspace+portal_container
(For example: For workspace "system" on portal container "portal" you will have tables like:
JCR_ISYSTEM_portal, JCR_VSYSTEM_portal, JCR_RSYSTEM_portal
and for workspace "system" on portal container "sampleportal" you will have tables like: JCR_ISYSTEM_sampleportal, JCR_VSYSTEM_sampleportal, JCR_RSYSTEM_sampleportal.
So total number of JCR tables is 3*5*X (3 is number of tables per workspace, 5 is number for workspaces in GateIn, X is number of portal containers))

So for workspace "portal-system" you need to change property like this:
<property name="db-tablename-suffix" value="PSYSTEM${container.name.suffix}"/>

And for workspace "portal-work" you need to change property like this:
<property name="db-tablename-suffix" value="PWORK${container.name.suffix}"/>

For workspace "system" this property actually doesn't exist, so you need to add it with value:
<property name="db-tablename-suffix" value="SYSTEM${container.name.suffix}"/>

3.d) Similar changes need to be done in repository configuration for WSRP. It's in file GATEIN_HOME/gatein/extensions/gatein-wsrp-integration.ear/extension-war.war/WEB-INF/conf/wsrp/repository-configuration.xml
So you need again to delete $container.name.suffix for both workspaces "wsrp-system" and "pc-system" as mentioned in (3.b)

And then you need to change property "db-tablename-suffix" for "wsrp-system" to be like this
<property name="db-tablename-suffix" value="WSRPSYS${container.name.suffix}"/>

And add this property for "pc-system" with value like this:


4) Known limitations:
4.a) Make sure that your portal containers has only character like letters and numbers in name.
For example: Your container could be named "sampleportal" but not "sample-portal" because of "-" character. This is due
to the fact that names of portal container is used in name of DB tables and indexes (See point 3.c).
Also it's possible that some databases doesn't support low letters in table/index names so you will need to use something like "SAMPLEPORTAL" as name of your portal container.
NOTE 1: I've tested on MySQL 5.5.32, where is ok to have low letters and so name "sampleportal" is fine.
NOTE 2: For more details see issue https://jira.exoplatform.org/browse/JCR-2252. Hopefully in future JCR version this limitation
will be removed and it would be possible to use anything for portal container name)

4.b) Shared DS setup probably won't work for you if you are using Oracle DB due to the fact that length of DB table/index is limited to 30 characters. So in case
 of Oracle, you will probably need to stick with "classic" setup with separated DS/database per portal container.