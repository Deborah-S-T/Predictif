/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package metier.service;

import dao.JpaUtil;
import metier.modele.Client;
import dao.ClientDao;
import dao.ConsultationDao;
import dao.EmployeDao;
import dao.MediumDao;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import metier.modele.Astrologue;
import metier.modele.Cartomancien;
import metier.modele.Consultation;
import metier.modele.Medium;
import metier.modele.ProfilAstral;
import metier.modele.Spirite;
import metier.modele.Employe;

/**
 *
 * @author dsteferra
 */
public class Service {
    
    private ProfilAstral calculProfilAstral(Client client) {
        JsonObject result = null;
        ProfilAstral profil = new ProfilAstral();
        
        try {
            // TODO: adapter l'URL de l'API et la liste des paramètres
            URI requestUri = URI.create(
                    "https://servif.insa-lyon.fr/WebDataGenerator/Astro"
                    + "?service=" + URLEncoder.encode("profil", StandardCharsets.UTF_8)
                    + "&key=" + URLEncoder.encode("ASTRO-01-M0lGLURBU0ktQVNUUk8tQjAx", StandardCharsets.UTF_8)
                    + "&prenom=" + URLEncoder.encode(client.getPrenom(), StandardCharsets.UTF_8)
                    + "&date-naissance=" + URLEncoder.encode(client.getDateDeNaissance().toString(), StandardCharsets.UTF_8)
            );

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder(requestUri).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                String body = httpResponse.body();
                //System.out.println(body);

                result = Json.createReader(new StringReader(body)).readObject();
                
                //System.out.println(result.toString());
                //{"profil":{"signe-zodiaque":"Capricorne","signe-chinois":"Chèvre","couleur":"Abricot","animal":"Kiwi"}}
                JsonObject obj = result.getJsonObject("profil");
                profil.setAnimal(obj.getString("animal"));
                profil.setCouleur(obj.getString("couleur"));
                profil.setSigneChinois(obj.getString("signe-chinois"));
                profil.setZodiac(obj.getString("signe-zodiaque"));
                
            } else {
                throw new IOException("HTTP Error Status Code " + httpResponse.statusCode());
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            profil = null;
        }
        
        return profil;
    }
    
    public Boolean inscrireClient(Client client) {
        Boolean clientInscrit = false; 
        try {
            System.out.println("metier.service.Service.inscrireClient()");
            ProfilAstral profil = new ProfilAstral();
            profil = calculProfilAstral(client);
            
            if (profil != null) {
                client.setProfil(profil);
            }
            else {
                throw new Exception("Le profil n'a pas pu être créé.");
            }
            
            
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            
            
            // si le client n'exite pas déja dans la base
            try {
                Client clientExistant = ClientDao.findByMailAndPassword( client.getMail(), client.getMotDePasse());
                clientInscrit = false;
                System.out.println("Le client est déjà inscrit.");
            }
            catch (Exception e) {
                
                ClientDao.create(client);
                JpaUtil.validerTransaction();
                clientInscrit = true;
            }
          
            //System.out.println(profil.toString());
            
            
        } catch (Exception e) {
            e.printStackTrace();
            JpaUtil.annulerTransaction();
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return clientInscrit;
    }
    
    public Client authentifierClient(String mail, String password) {
        Client client = null;
        try {
            System.out.println("metier.service.Service.authentifierClient()");
            
            JpaUtil.creerContextePersistance();

            client = ClientDao.findByMailAndPassword(mail, password);

            //System.out.println(profil.toString());
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return client;
    }
    
    public Employe authentifierEmploye(String mail, String password) {
        Employe employe = null;
        try {
            System.out.println("metier.service.Service.authentifierEmploye()");
            
            JpaUtil.creerContextePersistance();

            employe = EmployeDao.findByMailAndPassword(mail, password);

            //System.out.println(profil.toString());
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return employe;
    }
    
    public void initialiserMedium() {
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            
            //Gwenaelle
            Spirite med1 = new Spirite("Boule de cristal", "Gwenaelle", "F", "Spécialiste des grandes conversations au-delà de TOUTES les frontières");
            MediumDao.create(med1);
            
            //Professeur Tran
            Spirite med2 = new Spirite("Marc de café, boule de cristal, oreilles de lapin", "Professeur Tran", "H", "Votre avenir est devant vous : regardonds-le ensemble !");
            MediumDao.create(med2);
            
            //Mme Irma
            Cartomancien med3 = new Cartomancien("Mme Irma", "F", "Comprenez votre entourage grâce à mes cartes ! Résultats rapides");
            MediumDao.create(med3);
            
            //Endora
            Cartomancien med4 = new Cartomancien("Endora", "F", "Mes cartes répondront à toutes vos questions personnelles.");
            MediumDao.create(med4);
            
            //Serena
            Astrologue med5 = new Astrologue("Ecole Normale Supérieure d'Astrologie (ENS-Astro)", "2006", "Serena", "F", "Basée à  Champigny-sur-Marne, Serena vous révèlera votre avenir pour éclairer votre passé.");
            MediumDao.create(med5);
            
            //Mr M
            Astrologue med6 = new Astrologue("Institut des Nouveaux Savoirs Astrologiques (INSA)", "2010", "Mr M", "H", "Avenir, avenir, que nous réserves-tu ? N'attendez plus, demander à me consulter !");
            MediumDao.create(med6);

            JpaUtil.validerTransaction();
          
        } catch (Exception e) {
            e.printStackTrace(System.err);
            JpaUtil.annulerTransaction();
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
    }
    
    
    
    public void initialiserEmploye() {
        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            
            
            Employe e1 = new Employe("Guerin", "Yoan", "H", "0123456789", "yoan.guerin@insa-lyon.fr", "mdpyoan");
            EmployeDao.create(e1);
            
            Employe e2 = new Employe("Retourné", "Paul", "H", "0987654321", "paul.retourne@insa-lyon.fr", "mdppaul");
            EmployeDao.create(e2);

            
            JpaUtil.validerTransaction();
          
        } catch (Exception e) {
            e.printStackTrace(System.err);
            JpaUtil.annulerTransaction();
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
    }
    
    public Medium getMediumById(Long id) {
        Medium medium = null;
        try {
            System.out.println("metier.service.Service.getMediumById()");
            
            JpaUtil.creerContextePersistance();

            medium = MediumDao.findById(id);

            //System.out.println(profil.toString());
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return medium;
    }
    
    public List<Medium> getAllMedium() {
        List<Medium> mediums = new ArrayList<>();
        try {
            System.out.println("metier.service.Service.getAllMedium()");
            
            JpaUtil.creerContextePersistance();

            mediums = MediumDao.findAll();

            //System.out.println(profil.toString());
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return mediums;
    }
    
    public Boolean demanderConsultation(Client client, Medium medium) {
        Boolean employeAffecte = false;
        try {
            System.out.println("metier.service.Service.demanderConsultation()");
            
            JpaUtil.creerContextePersistance();
            
            Consultation consultation = new Consultation(client, medium);
            Employe employeLibre = EmployeDao.findEmployeLibre(medium.getGenre());
            consultation.setEmploye(employeLibre);
            
            employeLibre.setEstEnConsultation(Boolean.TRUE);
            employeLibre.appendListeConsultations(consultation);
            
            client.appendListeConsultations(consultation);
            
            JpaUtil.ouvrirTransaction();
            
            ConsultationDao.create(consultation);
            
            EmployeDao.update(employeLibre);
            ClientDao.update(client);
            
            employeAffecte = true;
            JpaUtil.validerTransaction();
        }
        catch (Exception e) {
            JpaUtil.annulerTransaction();
            e.printStackTrace(System.err);
            System.out.println("pas d'employe affecté");
        }
        finally {
            JpaUtil.fermerContextePersistance();
        }
        return employeAffecte;
    }
}
    
