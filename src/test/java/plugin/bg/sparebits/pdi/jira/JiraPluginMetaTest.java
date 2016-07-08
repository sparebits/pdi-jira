/*! ***************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package plugin.bg.sparebits.pdi.jira;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPluginType;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.IntLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.PrimitiveIntArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;

import bg.sparebits.pdi.domain.Api;
import bg.sparebits.pdi.domain.SearchApiMeta;


/**
 * @author Neyko Neykov 2015
 */
public class JiraPluginMetaTest {

    @Test
    public void empty() {
        JiraPluginMeta meta = new JiraPluginMeta();
        meta.setApiConfiguration("{\"key\":\"pjp-35\"}");
        SearchApiMeta searchMeta = (SearchApiMeta) meta.getApiConfiguration(Api.search);
        Assert.assertNotNull(searchMeta);
    }

    @BeforeClass
    public static void beforeClass() throws KettleException {
        PluginRegistry.addPluginType( TwoWayPasswordEncoderPluginType.getInstance() );
        PluginRegistry.init();
        String passwordEncoderPluginID =
            Const.NVL( EnvUtil.getSystemProperty( Const.KETTLE_PASSWORD_ENCODER_PLUGIN ), "Kettle" );
        Encr.init( passwordEncoderPluginID );
    }

    @Test
    public void testLoadSave() throws KettleException {
        List<String> attributes = Arrays.asList( new String[] { "ConnectionUrl", "Username", "Password", "Jql",
          "MaxResults", "StartPage", "OutputField", "FieldNames", "FieldTypes", "FieldExpressions", "Api",
          "ApiConfiguration", "Issue" } );
        Map<String, String> customGetters = new HashMap<String, String>();
        Map<String, String> customSetters = new HashMap<String, String>();

        Map<String, FieldLoadSaveValidator<?>> typeValidators = new HashMap<String, FieldLoadSaveValidator<?>>();
        Map<String, FieldLoadSaveValidator<?>> fieldValidators = new HashMap<String, FieldLoadSaveValidator<?>>();
        fieldValidators.put( "ConnectionUrl", new StringLoadSaveValidator() );
        fieldValidators.put( "Username", new StringLoadSaveValidator() );
        fieldValidators.put( "Password", new StringLoadSaveValidator() );
        fieldValidators.put( "Jql", new StringLoadSaveValidator() );
        fieldValidators.put( "MaxResults", new IntLoadSaveValidator() );
        fieldValidators.put( "StartPage", new IntLoadSaveValidator() );
        fieldValidators.put( "OutputField", new StringLoadSaveValidator() );
        fieldValidators.put( "FieldNames", new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 50 ) );
        fieldValidators.put( "FieldTypes", new PrimitiveIntArrayLoadSaveValidator( new IntLoadSaveValidator(), 50 ) );
        fieldValidators.put( "FieldExpressions", new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 50 ) );
        fieldValidators.put( "Api", new StringLoadSaveValidator() );
        fieldValidators.put( "ApiConfiguration", new StringLoadSaveValidator() );
        fieldValidators.put( "Issue", new StringLoadSaveValidator() );

        LoadSaveTester tester = new LoadSaveTester( JiraPluginMeta.class, attributes, customGetters, customSetters,
          fieldValidators, typeValidators );

        tester.testXmlRoundTrip();
        tester.testRepoRoundTrip();
    }
}
