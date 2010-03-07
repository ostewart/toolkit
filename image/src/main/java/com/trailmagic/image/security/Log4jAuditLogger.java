package com.trailmagic.image.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.AuditableAccessControlEntry;
import org.springframework.security.acls.domain.AuditLogger;

public class Log4jAuditLogger implements AuditLogger {
    private static final Logger log = LoggerFactory.getLogger(Log4jAuditLogger.class);

    public void logIfNeeded(boolean granted, AccessControlEntry ace) {
        if (ace instanceof AuditableAccessControlEntry) {
            if (!log.isDebugEnabled()) return;

            AuditableAccessControlEntry auditableAce = (AuditableAccessControlEntry) ace;

            if (granted && auditableAce.isAuditSuccess()) {
                log.debug("GRANTED due to ACE: " + ace);
            } else if (!granted && auditableAce.isAuditFailure()) {
                log.debug("DENIED due to ACE: " + ace);
            }
        }
    }
}
