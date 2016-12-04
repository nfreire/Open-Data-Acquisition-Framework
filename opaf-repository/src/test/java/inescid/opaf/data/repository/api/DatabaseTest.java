/* DatabaseTest.java - created on 18/07/2014, Copyright (c) 2011 The European Library, all rights reserved */
package inescid.opaf.data.repository.api;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.api.Record;

/**
 * 
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 18/07/2014
 */
public class DatabaseTest {

    
    @Test
    public void testPersonalRecordParsing() throws Exception {
        String dataBaseFolderPath="target/map-databases/test";
        FileUtils.deleteDirectory(new File(dataBaseFolderPath));
        
        Database db=new Database(new File(dataBaseFolderPath), AccessMode.WRITE);
        Assert.assertNull(db.getData(1L));
        
        Record rec = new Record(1L);
        rec.setDataToFields();
        rec.addField(1, "string");
        rec.addField(2, "123");
        db.add(rec);
        
        Iterable<Object[]> search = db.search(1, "string");
        Iterator<Object[]> iterator = search.iterator();
        Assert.assertTrue(iterator.hasNext());
        System.out.println(iterator.next());
        
        Assert.assertNotNull(db.getData(1L));

        
        //
//        Iterable<Object> results = db.search(ViafField.NAME_FORM,ViafField.NAME_FORM.normalizeValue("Flory, A."));
//        Iterator<Object> iterator = results.iterator();
//        Assert.assertTrue(iterator.hasNext());
//        results = db.search(ViafField.EXTERNAL_ID,ViafField.EXTERNAL_ID.normalizeValue("no2010132550"));
//        Assert.assertTrue(iterator.hasNext());
//        Object data = db.getData(iterator.next());
//        Assert.assertNotNull(data);
//
//        MetaDataRecordBeanBytesConverter converter=MetaDataRecordBeanBytesConverter.INSTANCE;
//        System.out.println( converter.decode((byte[])data) );
    }
}
