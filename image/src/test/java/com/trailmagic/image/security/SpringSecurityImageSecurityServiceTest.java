package com.trailmagic.image.security;

import com.trailmagic.image.ImageGroupRepository;
import junit.framework.TestCase;
import org.mockito.Mockito;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;

public class SpringSecurityImageSecurityServiceTest extends TestCase {
    public void testCreatesNewAclWithOwner() {
        new SpringSecurityImageSecurityService(Mockito.mock(MutableAclService.class), Mockito.mock(ImageGroupRepository.class), Mockito.mock(ObjectIdentityRetrievalStrategy.class));

    }
//
//    public void testMasks() {
//        assertEquals(SimpleAclEntry.READ_WRITE_CREATE_DELETE | SimpleAclEntry.ADMINISTRATION, new CumulativePermission().set(BasePermission.READ).set(BasePermission.WRITE).set(BasePermission.CREATE).set(BasePermission.DELETE).set(BasePermission.ADMINISTRATION).getMask());
//    }
//    public void testRead() {
//        assertEquals(SimpleAclEntry.READ, BasePermission.READ.getMask());
//    }
//    public void testWrite() {
//        assertEquals(SimpleAclEntry.WRITE, BasePermission.WRITE.getMask());
//    }
//    public void testCreate() {
//        assertEquals(SimpleAclEntry.CREATE, BasePermission.CREATE.getMask());
//    }
//    public void testDelete() {
//        assertEquals(SimpleAclEntry.DELETE, BasePermission.DELETE.getMask());
//    }
//    public void testAdmin() {
//        assertEquals(SimpleAclEntry.ADMINISTRATION, BasePermission.ADMINISTRATION.getMask());
//    }
//
//    public void testCumulative() {
//        assertEquals(SimpleAclEntry.ADMINISTRATION, new CumulativePermission().set(BasePermission.ADMINISTRATION).getMask());
//    }
}
