package com.akasoft.poneyrox.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 *  DAO.
 *  Classe permettant l'accès à la couche persistente.
 */
@Service
@Transactional
public abstract class AbstractDAO {
    /**
     *  Générateur de sessions.
     */
    private SessionFactory factory;

    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public AbstractDAO(SessionFactory factory) {
        this.factory = factory;
    }

    /**
     *  Retourne la session NHibernate courante.
     *  @return Session NHibernate.
     */
    protected Session getSession() {
        return this.factory.getCurrentSession();
    }
}
