package com.trailmagic.image.ui;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.beans.BeansException;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.HibernateException;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.ByteArrayInputStream;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.servlet.ServletException;

import com.trailmagic.user.*;
import com.trailmagic.image.util.ImagesParser;
import com.trailmagic.image.*;

public class ImageImportController extends SimpleFormController {
    private static final String IMAGES_PARSER_BEAN = "imagesParser";

    protected ModelAndView onSubmit(HttpServletRequest req,
                                    HttpServletResponse res,
                                    Object command,
                                    BindException errors)
        throws Exception {

        ImageImportBean bean = (ImageImportBean)command;

        if ( bean == null ) {
            // huh?
        }

        byte[] data = bean.getImagesData();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ImagesParser handler =
            (ImagesParser)getApplicationContext().getBean(IMAGES_PARSER_BEAN);


        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        parser.parse(bis, handler);
        
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
    }
}
