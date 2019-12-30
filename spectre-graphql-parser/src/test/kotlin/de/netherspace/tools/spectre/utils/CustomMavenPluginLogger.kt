package de.netherspace.tools.spectre.utils

import org.apache.maven.plugin.logging.Log
import org.slf4j.LoggerFactory

class CustomMavenPluginLogger : Log {

    private val logger = LoggerFactory.getLogger(CustomMavenPluginLogger::class.java)

    override fun warn(p0: CharSequence?) {
        logger.warn(p0!!.toString())
    }

    override fun warn(p0: CharSequence?, p1: Throwable?) {
        logger.warn(p0!!.toString(), p1!!)
    }

    override fun warn(p0: Throwable?) {
        logger.warn("", p0!!)
    }

    override fun info(p0: CharSequence?) {
        logger.info(p0!!.toString())
    }

    override fun info(p0: CharSequence?, p1: Throwable?) {
        logger.info(p0!!.toString(), p1!!)
    }

    override fun info(p0: Throwable?) {
        logger.info("", p0!!)
    }

    override fun isInfoEnabled(): Boolean = true

    override fun isErrorEnabled(): Boolean = true

    override fun isWarnEnabled(): Boolean = true

    override fun error(p0: CharSequence?) {
        logger.error(p0!!.toString())
    }

    override fun error(p0: CharSequence?, p1: Throwable?) {
        logger.error(p0!!.toString(), p1!!)
    }

    override fun error(p0: Throwable?) {
        logger.error("", p0!!)
    }

    override fun isDebugEnabled(): Boolean = true

    override fun debug(p0: CharSequence?) {
        logger.debug(p0!!.toString())
    }

    override fun debug(p0: CharSequence?, p1: Throwable?) {
        logger.debug(p0!!.toString(), p1!!)
    }

    override fun debug(p0: Throwable?) {
        logger.debug("", p0!!)
    }
}
