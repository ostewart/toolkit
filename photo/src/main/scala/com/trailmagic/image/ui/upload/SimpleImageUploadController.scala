package com.trailmagic.image.ui.upload

import com.trailmagic.image.ImageGroup
import com.trailmagic.image.ImageService
import com.trailmagic.image.Photo
import com.trailmagic.util.SecurityUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import scala.collection.JavaConverters._
import scala.collection.mutable.Map
import org.springframework.http.HttpStatus

@Controller
@RequestMapping(Array("/upload"))
class SimpleImageUploadController @Autowired()(imageService: ImageService, securityUtil: SecurityUtil) {
  @RequestMapping
  def showForm: ModelAndView = {
    val imageGroup: ImageGroup = imageService.findOrCreateDefaultRollForUser(securityUtil.getCurrentUser)
    
    val model = Map("imageGroup" -> imageGroup, "nextFramePosition" -> imageGroup.nextFramePosition)
    new ModelAndView("groupUpload", model.asJava)
  }

  @RequestMapping(method = Array(RequestMethod.POST))
  def handleUpload(@RequestParam(value = "pos", required = false) position: java.lang.Integer,
                   @RequestParam(value = "fileName") fileName: String,
                   req: HttpServletRequest, res: HttpServletResponse) {
    val image: Photo = position match {
      case null => imageService.createDefaultImage(fileName)
      case _ => imageService.createImageAtPosition(fileName, req.getInputStream, position.intValue)
    }

    imageService.createManifestations(image, req.getInputStream)
    res.setStatus(HttpStatus.SEE_OTHER.value)
    res.addHeader("Location", res.encodeRedirectURL(req.getContextPath + "/rolls/" + securityUtil.getCurrentUser.getScreenName + "/uploads/" + image.getId))
  }
}