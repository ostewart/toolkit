package com.trailmagic.image.ui

import com.trailmagic.image.security.ImageSecurityService
import com.trailmagic.user.User
import com.trailmagic.user.UserRepository
import com.trailmagic.web.util.WebRequestTools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.HashSet
import scala.collection.JavaConversions._
import com.trailmagic.image._

@Controller
class ImageGroupDisplayController @Autowired()(private val imageGroupRepository: ImageGroupRepository,
                                               private val imageSecurityService: ImageSecurityService,
                                               private val userRepository: UserRepository,
                                               private val webRequestTools: WebRequestTools,
                                               private val imageService: ImageService) {
  @InitBinder
  def initBinder(binder: WebDataBinder): Unit = {
    binder.registerCustomEditor(classOf[ImageGroupType], new ImageGroupTypeUrlComponentPropertyEditor)
  }

  @RequestMapping(value = Array("/{groupType}/{screenName}/{groupName}"), method = Array(RequestMethod.GET))
  def handleDisplayGroup(request: HttpServletRequest, response: HttpServletResponse,
                         @PathVariable("groupType") groupType: ImageGroupType,
                         @PathVariable("screenName") screenName: String,
                         @PathVariable("groupName") groupName: String,
                         model: ModelMap): ModelAndView = {
    if (webRequestTools.preHandlingFails(request, response, true)) return null
    val owner: User = userRepository.getByScreenName(screenName)
    imageGroupRepository.getByOwnerNameAndTypeWithFrames(owner, groupName, groupType) match {
      case group: ImageGroup => {
        model.addAttribute("imageGroup", group)
        model.addAttribute("imageGroupIsPublic", imageSecurityService.isPublic(group))
        model.addAttribute("frames", group.getFrames)
        if (request.getParameter("createRoll") != null) {
          return new ModelAndView("newGroup")
        }
        return new ModelAndView("imageGroup", model)
      }
      case null => throw new ImageGroupNotFoundException(groupType.getDisplayString + " not found: " + groupName)
    }
  }

  @RequestMapping(value = Array("/{groupType}/{screenName}/{groupName}"), method = Array(RequestMethod.POST))
  def handleCreateNewGroupWithImages(@PathVariable("screenName") screenName: String,
                                     @RequestParam("rollName") rollName: String,
                                     @RequestParam("selectedFrames") selectedFrameIds: HashSet[Long]): ModelAndView = {
    imageService.createRollWithFrames(rollName, asScalaSet(selectedFrameIds))
    return new ModelAndView("redirect:/rolls/" + screenName + "/" + rollName)
  }
}