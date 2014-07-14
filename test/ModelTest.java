import org.junit.*;

import java.util.*;

import play.Configuration;
import play.Logger;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import models.*;

import com.avaje.ebean.*;

public class ModelTest {
    
    private String formatted(Date date) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
/*
    @Test
    public void findById() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Computer macintosh = Computer.find.byId(21l);
               assertThat(macintosh.name).isEqualTo("Macintosh");
               assertThat(formatted(macintosh.introduced)).isEqualTo("1984-01-24");
           }
        });
    }
  */   
    @Test
    public void configurations() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
           public void run() {
        	   Configuration config =play.Play.application().configuration();
               Logger.info(config.getString("site.address"));
               Logger.info(config.getString("smtp.user"));
           }
        });
    }
   
    
}
