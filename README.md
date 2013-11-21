many-portal-containers
======================

Util to create and deploy many portal containers into your GateIn/JPP

EXAMPLE STEPS FOR CREATE AND DEPLOY 10 SAMPLE CONTAINERS:

1) Build with "mvn clean install"

2) Configure your GateIn to run with MySQL. It means:

2.a) add GATEIN_HOME/modules/com/mysql module with MySQL JDBC driver

2.b) add something like this into appropriate section of GATEIN_HOME/standalone/configuration/standalone.xml:


                    <driver name="mysql" module="com.mysql">
                        <xa-datasource-class>com.mysql.jdbc.Driver</xa-datasource-class>
                    </driver>

2.c) Configure datasources into standalone.xml like this (Assumption is that your MySQL username and password is "portal/portal" and that you use DB named "portal1" for your main portal):

                <datasource jndi-name="java:/jdbcidm_portal" pool-name="IDMPortalDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:mysql://localhost/portal1</connection-url>
                    <driver>mysql</driver>
                    <security>
                        <user-name>portal</user-name>
                        <password>portal</password>
                    </security>

	            <transaction-isolation>TRANSACTION_READ_COMMITTED</transaction-isolation>
         	    <pool>
	                  <min-pool-size>10</min-pool-size>
        	          <max-pool-size>100</max-pool-size>
        	          <prefill>true</prefill>
        	    </pool>
	            <statement>
	                  <prepared-statement-cache-size>100</prepared-statement-cache-size>
        	          <share-prepared-statements>true</share-prepared-statements>
        	    </statement>
	        </datasource>
                <datasource jndi-name="java:/jdbcjcr_portal" pool-name="JCRPortalDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:mysql://localhost/portal1</connection-url>
                    <driver>mysql</driver>
                    <security>
                        <user-name>portal</user-name>
                        <password>portal</password>
                    </security>

	            <transaction-isolation>TRANSACTION_READ_COMMITTED</transaction-isolation>
        	    <pool>
                	  <min-pool-size>10</min-pool-size>
	                  <max-pool-size>100</max-pool-size>
        	    	  <prefill>true</prefill>
	            </pool>
         	    <statement>
	                  <prepared-statement-cache-size>100</prepared-statement-cache-size>
        	          <share-prepared-statements>true</share-prepared-statements>
        	   </statement>
                </datasource>

3) Run script "scripts/mysql-refresh.sh 10". Then login into MySQL and copy output of the script into MySQL console. That will drop and create all databases needed for sample portals

4) Run script "scripts/create-ears.sh 1 10". This will create 10 EARS with sampleportal in current directory.

5) Run command "mv sampleportal*.ear $GATEIN_HOME/gatein/extensions/" . $GATEIN_HOME should point to your GateIn directory.

6) Run script "scripts/create-xml-from-template.sh datasources-xml 10"
Then copy the content of the file datasources-xml-10 into appropriate section of GATEIN_HOME/standalone/configuration/standalone.xml .
Then you can delete datasources-xml-10 file.

7) Run script "scripts/create-xml-from-template.sh security-domains-xml 10"
Then copy the content of the file security-domains-xml-10 into appropriate section of GATEIN_HOME/standalone/configuration/standalone.xml .
Then you can delete security-domains-xml-10 file.

8) Delete GATEIN_HOME/standalone/data GATEIN_HOME/standalone/tmp GATEIN_HOME/standalone/log . 
It may be also good to setup root password in GATEIN_HOME/standalone/configuration/gatein/configuration.properties, otherwise the setup.jsp will be shown to you for each container (In our case with 11 portal containers, you will need to setup root password in UI 11 times). To setup root password "gtn" you just need to add this into configuration.properties:
gatein.portal.setup.initialpassword.root=6MSyXIj3kkQ=
NOTE: (Just for quick testing. In production, you should use different root password then "gtn")

9) Then run GATEIN and enjoy containers under:
http://localhost:8080/portal
http://localhost:8080/sampleportal1
http://localhost:8080/sampleportal2
...
http://localhost:8080/sampleportal10

Note: We have one portal container and 10 sampleportal containers. So 11 portal containers totally.

