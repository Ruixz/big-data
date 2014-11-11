package com.udf.pig;
 
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.pig.*;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.*;
import org.apache.pig.data.*;
 
import java.io.IOException;
import java.util.*;
 
/**
 * A loader class of pig.
 *
 * @author rui
 */
 
public class MyLoader extends LoadFunc {
  protected RecordReader recordReader = null;
 
  @Override
  public void setLocation(String s, Job job) throws IOException {
    FileInputFormat.setInputPaths(job, s);
  }
 
  @Override
  public InputFormat getInputFormat() throws IOException {
    return new PigTextInputFormat();
  }
 
  @Override
  public void prepareToRead(RecordReader recordReader, PigSplit pigSplit) throws IOException {
    this.recordReader = recordReader;
  }
 
  @Override
  public Tuple getNext() throws IOException {
    try {
      boolean flag = recordReader.nextKeyValue();
      if (!flag) {
        return null;
      }
      Text value = (Text) recordReader.getCurrentValue();
      String[] strArray = value.toString().split("::");
      List lst = new ArrayList<String>();
      int i = 0;
      for (String singleItem : strArray) {
        lst.add(i++, singleItem);
      }
      return TupleFactory.getInstance().newTuple(lst);
    } catch (InterruptedException e) {
      throw new ExecException("Read data error", PigException.REMOTE_ENVIRONMENT, e);
    }
  }
}