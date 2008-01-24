package de.erdesignerng.test.mimer;

//If you don't already have Axis, go to xml.apache.org and download this excellent SOAP implementation
import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import javax.xml.namespace.QName;
import java.net.URL;

/**
 * 
 * @author olle
 */
public class ValidateSQL99Client {

    // This method makes the web service call
    // If you want to you can create a web service proxy from the WSDL file,
    // 9 times out of 10, that's what you really want to do.
    public ValidatorResult callSQL99Validator(URL a_url, int a_sessionId, int a_sessionKey, String a_sqlStatement,
            String a_resultType) throws RemoteException, ServiceException {

        Service l_service = new Service();
        Call l_call = (Call) l_service.createCall();

        // Set the target server and name space
        l_call.setTargetEndpointAddress(a_url);
        l_call.setOperationName(new QName("SQL99Validator", "validateSQL"));

        // Add the parameter names and types
        // Use the session Id you got from the openSession call here
        l_call.addParameter("a_sessionId", XMLType.XSD_INT, ParameterMode.IN);
        // Use the session key you got from the openSession call here
        l_call.addParameter("a_sessionKey", XMLType.XSD_INT, ParameterMode.IN);
        // The SQL statement to be validated against the standard
        l_call.addParameter("a_sqlStatement", XMLType.XSD_STRING, ParameterMode.IN);
        // The format of the result. This must be "text" or "html".
        // Hopefully some type of XML format will be available as well
        l_call.addParameter("a_resultType", XMLType.XSD_STRING, ParameterMode.IN);

        QName l_qn = new QName("http://sqlvalidator.mimer.com/v1.0", "ValidatorResult");
        // QName l_qn = new QName( "http://sqlvalidator.mimer.com/v1",
        // "ValidatorResult" );

        l_call.registerTypeMapping(ValidatorResult.class, l_qn, new org.apache.axis.encoding.ser.BeanSerializerFactory(
                ValidatorResult.class, l_qn), new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                ValidatorResult.class, l_qn));

        // Set the return type
        l_call.setReturnType(l_qn);

        Object l_ret = l_call.invoke(new Object[] { Integer.valueOf(a_sessionId), Integer.valueOf(a_sessionKey),
                a_sqlStatement, a_resultType });

        return (ValidatorResult) l_ret;
    }

    public SessionData openSession(URL a_url, String a_userName, String a_password, String a_callingProgram,
            String a_callingProgramVersion, String a_targetDbms, String a_targetDbmsVersion,
            String a_connectionTechnology, String a_connectionTechnologyVersion, int a_interactive)
            throws RemoteException, ServiceException {

        Service l_service = new Service();
        Call l_call = (Call) l_service.createCall();

        // Set the target server and name space
        l_call.setTargetEndpointAddress(a_url);
        l_call.setOperationName(new QName("SQL99Validator", "openSession"));

        // Supply the user name. If you use anonymous you will be logged in and
        // the pw will be ignored
        l_call.addParameter("a_userName", XMLType.XSD_STRING, ParameterMode.IN);
        // The pw. If user name is anonymous this can be anything. But it has to
        // be supplied anyway.
        l_call.addParameter("a_password", XMLType.XSD_STRING, ParameterMode.IN);
        // The name of the calling client program.
        // This is optional. If you don't want to give out this info, please
        // enter "N/A"
        l_call.addParameter("a_callingProgram", XMLType.XSD_STRING, ParameterMode.IN);
        // And the version of the calling program.
        // This is optional. If you don't want to give out this info, please
        // enter "N/A"
        l_call.addParameter("a_callingProgramVersion", XMLType.XSD_STRING, ParameterMode.IN);
        // The target DBMS, could be Mimer SQL Engine, Oracle, ...
        // This is optional. If you don't want to give out this info, please
        // enter "N/A"
        l_call.addParameter("a_targetDbms", XMLType.XSD_STRING, ParameterMode.IN);
        // The version of the target DBMS
        // This is optional. If you don't want to give out this info, please
        // enter "N/A"
        l_call.addParameter("a_targetDbmsVersion", XMLType.XSD_STRING, ParameterMode.IN);
        // The connection Technology used, could be ODBC, JDBC, ADO
        // This is optional. If you don't want to give out this info, please
        // enter "N/A"
        l_call.addParameter("a_connectionTechnology", XMLType.XSD_STRING, ParameterMode.IN);
        // Version
        // This is optional. If you don't want to give out this info, please
        // enter "N/A"
        l_call.addParameter("a_connectionTechnologyVersion", XMLType.XSD_STRING, ParameterMode.IN);
        // Set this to 1 if your application is interactive where the user
        // enters queries and then runs them
        // Set it to 2 if it is non interactive, such as for instance a JDBC
        // Bridge driver that intercepts SQL
        l_call.addParameter("a_interactive", XMLType.XSD_INT, ParameterMode.IN);

        QName l_qn = new QName("http://sqlvalidator.mimer.com/v1.0", "SessionData");
        // QName l_qn = new QName( "http://sqlvalidator.mimer.com/v1",
        // "SessionData" );

        l_call.registerTypeMapping(SessionData.class, l_qn, new org.apache.axis.encoding.ser.BeanSerializerFactory(
                SessionData.class, l_qn), new org.apache.axis.encoding.ser.BeanDeserializerFactory(SessionData.class,
                l_qn));

        // Set the return type
        l_call.setReturnType(l_qn);

        Object l_ret = l_call.invoke(new Object[] { a_userName, a_password, a_callingProgram, a_callingProgramVersion,
                a_targetDbms, a_targetDbmsVersion, a_connectionTechnology, a_connectionTechnologyVersion,
                Integer.valueOf(a_interactive) });

        return (SessionData) l_ret;
    }

    public static void main(String[] args) {
        try {
            // For testing on the local machine
            // URL l_url = new URL("http://localhost:8081/services");

            // For testing on the live server
            // URL l_url = new
            // URL("http://sqlvalidator.mimer.com/beta2/services");
            URL l_url = new URL("http://sqlvalidator.mimer.com/v1/services");

            // Create self and
            ValidateSQL99Client l_valSql = new ValidateSQL99Client();

            SessionData l_sd = l_valSql.openSession(l_url, "anonymous", "doesn't matter", "OlleClient", "8534",
                    "Mimer SQL Engine", "8.2.4g", "JDBC", "2.0", 2);

            int l_session = l_sd.getSessionId();
            int l_key = l_sd.getSessionKey();

            // Set the url for subsequent calls
            // This is to allow for load balancing to a server on the other side
            // of the world
            l_url = new URL(l_sd.getTarget());

            // make a few calls
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "select * from tab1", "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "insert", "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "select", "html"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key,
                    "select * from t1 where a like \"a\"", "html"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "insert into t1 values (1,2,3)",
                    "text"));
            System.out
                    .println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "insert t1 values (1,2,3)", "html"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "delete from t1 where a > 1",
                    "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "delete t1 where a < 1", "html"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key,
                    "insert into t2 (a,b,c) values (1, 2, 3)", "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key,
                    "insert ito t2 (a,b,c) values (1, 2, 3)", "html"));
        } catch (Exception e) {
            if (e instanceof AxisFault) {
                System.err.println(((AxisFault) e).dumpToString());
            } else {
                e.printStackTrace();
            }
        }
    }
};
