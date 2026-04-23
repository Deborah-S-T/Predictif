/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.predictif;

import dao.JpaUtil;
import java.time.LocalDate;
import java.util.List;
import metier.modele.Client;
import metier.modele.Consultation;
import metier.modele.Employe;
import metier.modele.Medium;
import metier.service.Service;
import util.Saisie;

/**
 *
 * @author dsteferra
 */
public class Predictif {

    public static void main(String[] args) {
        JpaUtil.creerFabriquePersistance();
        Service service = new Service();
        service.initialiserMedium();
        service.initialiserEmploye();
        
        scenarioTest();
        
        JpaUtil.fermerFabriquePersistance();
    }

    
    private static void scenarioTest () {
        Service service = new Service();
        
        //Inscription et authentification d'un client
        System.out.println("Inscription et authentification\n-------------------------------------------------");
        LocalDate date = LocalDate.parse("1802-02-26");
        Client c1 = new Client("victor.hugo@insa-lyon.fr", "Hugo", "Victor", date, "13 rue Albert Einstein", "01 23 45 67 89", "m0tdepasse1");
        Boolean resultat = service.inscrireClient(c1);
        assert resultat;
        Client c12 = service.authentifierClient("victor.hugo@insa-lyon.fr", "m0tdepasse1");
        System.out.println("\n-------------------------------------------------");
        
        // Inscription et authentification du deuxième client
        System.out.println("Inscription et authentification\n-------------------------------------------------");
        LocalDate date2 = LocalDate.parse("1802-02-26");
        Client c2 = new Client("tableau.hugo@insa-lyon.fr", "Hugo", "Tableau", date2, "12 rue Albert Einstein", "01 33 55 77 99", "m0tdepasse1");
        Boolean resultat2 = service.inscrireClient(c2);
        assert resultat2;
        Client c22 = service.authentifierClient("tableau.hugo@insa-lyon.fr", "m0tdepasse1");
        System.out.println("\n-------------------------------------------------");
        
        System.out.println("liste des mediums\n-------------------------------------------------");
        //On récupère la liste des mediums
        List<Medium> mediums = service.getAllMedium();
        System.out.println(mediums);
        System.out.println("\n-------------------------------------------------");
        
        System.out.println("Choix du medium et demande de la consultation\n-------------------------------------------------");
        //Il choisit un medium et demande une consultation
        long id_medium = 2;
        Medium medium = service.getMediumById(id_medium);
        System.out.println(medium);
        service.demanderConsultation(c12, medium);
        
        service.demanderConsultation(c22, medium);
        System.out.println("\n-------------------------------------------------");
        
        
        System.out.println("Adresses clients\n-------------------------------------------------");
        // on devra peut-être l'effacer vu que c'est juste récupérer les adresses disctinctes
        System.out.println(service.getListeAdressesClients());
        System.out.println("\n-------------------------------------------------");
        
        System.out.println("se mettre pret\n-------------------------------------------------");
        Employe employe = service.authentifierEmploye("paul.retourne@insa-lyon.fr", "mdppaul");
        service.seMettrePret(employe);
        System.out.println("\n-------------------------------------------------");
        
        /*String invite = "";
        Saisie.lireChaine(invite);*/
    }
    
//    private static void testerHistoConsultation() {
//        
//        service.getHistoriqueConsultationsById(c12);
//    }
}
