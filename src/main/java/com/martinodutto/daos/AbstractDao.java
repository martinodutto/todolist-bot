package com.martinodutto.daos;

import com.martinodutto.services.DbManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDao {

    @Autowired
    protected DbManager dbManager;

    protected Logger logger = LogManager.getLogger(this.getClass());
}
