package com.trailmagic.image.ui;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.trailmagic.user.*;
import com.trailmagic.image.util.ImagesParser;
import com.trailmagic.image.*;

public class ImageImportController extends SimpleFormController {
    private static final String IMAGES_PARSER_BEAN = "imagesParser";

    private static Logger s_logger =
        Logger.getLogger(ImageImportController.class);

    public ImageImportController() {
    }

    protected ModelAndView onSubmit(HttpServletRequest req,
                                    HttpServletResponse res,
                                    Object command,
                                    BindException errors)
        throws Exception {
        try {
            s_logger.debug("onSubmit called.");
            ImageImportBean bean = (ImageImportBean)command;

            if ( bean == null ) {
                throw new Exception("null command in ImageImportController");
            }

            byte[] data = bean.getImagesData();
            s_logger.debug("imagesData: " + data);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ImagesParser handler =
                (ImagesParser)getApplicationContext().getBean(IMAGES_PARSER_BEAN);


            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(bis, handler);
        } catch (Exception e) {
            s_logger.warn("Exception in onSubmit", e);
            throw e;
        }
        
        return super.onSubmit(req, res, command, errors);
    }


    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder)
        throws ServletException {

        // to actually be able to convert Multipart instance to byte[]
        // we have to register a custom editor (in this case the
        // ByteArrayMultipartEditor)
        binder.registerCustomEditor(byte[].class,
                                    new ByteArrayMultipartFileEditor());
        // now Spring knows how to handle multipart object and convert them

        binder.setRequiredFields(new String[] {"imagesData"});
    }
}
