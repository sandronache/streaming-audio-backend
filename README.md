# GlobalWaves

    Proiectul reprezinta un **backend** pentru o aplicatie de ascultat muzica si podcasturi, avand multiple functionalitati similare cu cele ale aplicatiei **Spotify**.

    Cele mai importante dintre **features** sunt:

- **player:**
  
  * cu acesta se pot reda atat **piese** cat si **podcasturi**.
  
  * se poate pune **pauza**, pe **repeat** si chiar se poate activa functia de **shuffle**, da **inainte** sau **inapoi** in cazul in care se asculta un **playlist** sau un **album**.
  
  El se ocupa pe fundal, in mod constant, de redare.

- **pagini:**
  
  - **Home Page**: unde apar recomandari.
  
  - **Liked Content Page**: unde sunt piese la care utilizatorul da **like** si playlisturi pe care le urmareste.
  
  - se pot efectua actiuni precum: **inapoi** si **inainte**.
  
  Utilizatorul se poate afla la un moment de timp pe una din pagini si poate accesa continutul acesteia.

- **utilizatori:**
  
  - **Artist**: care in esenta este tot o pagina, unde putem gasi detalii despre artist, albumele lui, evenimentele planificate si lista de articole vestimentare personalizate.
    
    - un artist isi poate modifica datele prin **adaugare** si **stergere** de albume, etc.
  
  - **Host**: la fel ca la artist, doar ca aici avem podcasturi ca si continut.
    
    - poate realiza aceleasi actiuni ca si un artist.
  
  - **User**:
    
    * cel care se bucura de continut.
    
    * isi poate crea si modifica **playlisturi**.
  
  Un utilizator poate fi de asemenea si **offline**, moment in care playerul se opreste si actiuni precum accesarea paginilor nu sunt posibile.

- **wrapped:**
  
  - utilizatorul poate cere aceasta statistica si va vedea ce artisti a ascultat, ce **genuri**, melodii, albume, dar si podcasturi.

- **search and select:**
  
  - un utilizator poate cauta orice este disponibil in baza de date a aplicatiei: piese, podcasturi, **playlisturi publice**, artisti, albume, etc.
  
  - din raspunsuri, acesta poate selecta un element si in functie de element sa aplice ulterior actiuni:
    
    - daca este piesa, sa dea play sau sa o adauge la un playlist, de exemplu.
    
    - daca este o pagina, a unui artist de exemplu, sa o acceseze.
    
    - etc.

    Proiectul a fost realizat in cadrul cursului Programare Orientata pe Obiecte in anul II, din cadrul Facultatii Automatica si Calculatoare, parte din UNSTPB. 

    In surse se regaseste si scheletul de la care am pornit proiectul, mai exact partea de citire a input-ului, afisare a output-ului si testarea functionalitatii cu ajutorul unor teste, unele simple iar altele complexe.

    Input-ul si output-ul sunt in format **JSON**, input-ul fiind **libraria** initiala (piese, podcasturi si useri) si comenzile in ordinea realizarii lor, urmand ca output-ul sa fie rezultatele acestora.

    Proiectul a fost realizat in trei etape de-a lungul a unui intreg semestru. La fiecare etapa aveam de adaugat alte functionalitati si folosindu-ne de cunostintele noi acumulate, sa refactorizam codul. Cu cat codul era mai usor de extins, cu atat modificarile erau mai putine.

    Mai jos se regasesc README-urile de la ultimele doua etape, unde se gasesc detalii despre implementare, cum am gandit si de ce:

---

# README etapa 1 si 2:

## <u>Design</u>

* Pages:
  * Pentru pagini am gandit un design care sa poata fi extins prin adaugarea accesibila a altor tipuri de pagini;
  * Astfel, am creat **Interfata Page** care este implementata momentan de urmatoarele patru pagini: **HomePage**, **LikedContentPage**, **Artist**, **Host**.
  * Pentru Artist si Host nu am creat o clasa separata, si implicit un obiect, deoarece elementele continute de o pagina sunt incluse in totalitate in informatiile artistului/hostului, iar folosind insasi obiectul artistului/hostului, care este o referinta, am asigurat ca la orice moment de timp pagina pe care se afla userul obisnuit, in cazul in care se afla pe pagina unui artist/host, aceasta prezinta datele actuale. 
  * Pentru a separa comportamente diferite in cadrul programului am folosit design pattern-ul **Visitor**. Astfel am creat un visitor, care este acceptat, drept visitor, de catre interfata **Page**. Acest visitor este implementat in doua feluri:
    * Prima implementare, **UpdateVisitor**, se ocupa de actualizarea datelor de pe pagini, sa fie de actualitate, inainte de printare. Avand in vedere implementarea artistilor si al hostilor, in cazul acestora nu se schimba nimic, dar in viitor daca va trebui sa fie si ei modificati, din punct de vedere al implementarii va fi usor.
    * Cea de a doua, **DisplayVisitor** se ocupa de afisarea in functie de pagina si in functie de formatul impus.
* Users:
  * La aceasta etapa s-au mai adaugat 2 useri, **artist** si **host**, motiv pentru care am creat o **Interfata** **UserDatabase** care sa fie implementata de catre toti cei trei useri. De asemenea am folosit si un **Factory** care are rolul de a crea un user in functie de tipul oferit.
* Comenzi:
  * Am realizat si modificari cu privinta la interfetele/clasele abstracte in care sunt incluse comenzile. Atfel, am creat o **Interfata** **Commands** care are o functie **startCommand**. Aceasta interfata este implementata de toate comenzile.
  * Avem si o clasa abstracta, **PlayerRelatedCommands** care implementeaza interfata Commands. Aceasta adauga cateva metode in plus: **getCurrentPlayer**, **updatePlayer** si este extinsa de toate comenzile care  aplica modificari unui player sau tuturor. Am ales acest design pentru a nu repeta cod si pentru a folosi mostenirea in scopul punerii in comun a unei functionalitati comune. Fiind vorba doar de functionalitate, am ales sa folosesc o clasa abstracta.

## Flow

* Programul incepe prin a crea aplicatia, oferindu-i library-ul de input, comenzile si array-ul de outputs.
* Aplicatia foloseste un **Eager** **Singleton** prin care o instanta este creata la rularea programului. Avand constructorul private se asigura ca doar acea instanta va fi folosita de-a lungul programului. Am ales Eager Singleton deoarece aplicatia este punctul in sine de plecare deci instanta ei se va folosi rapid.
* In continuare, programul instantiaza biblioteca interna a aplicatiei cu informatiile oferite la input, care ulterior vor fi folosite si modificate.
* Dupa constructia aplicatiei aplicatia porneste si comenzile sunt preluate pe rand si executate.
* Majoritatea implementarii din cadrul primei etape ramane neschimbata, in schimb am adaugat urmatoarele (semnificative):
  * Playerul poate acum sa dea play si la un album, asadar am copiat comportamentul playlistului si l-am pus si pentru un album;
  * Search si select se poate face acum si de artist/host/album;
  * In cazul in care se selecteaza un artist/host se seteaza noua pagina a utilizatorului normal si programul sterge selectia;
  * Cele mai semnificative comenzi din etapa 2 sunt stergerea unui album/ a unui user, despre care nu o sa detaliez deoarece am pus in cod comentariu cu detalii in legatura cu abordarea la fiecare caz tratat;

# Adaugari etapa 3:

## Motivatie Design:

* Factory: Am folosit factory design pattern la crearea userilor (mai multe detalii despre cum am facut asta se afla mai sus). Am folosit acest pattern pentru a nu ma ingrijora cu realizarea obiectelor de care am nevoie, factory-ul primeste tipul de obiect pe care il vreau si acesta imi returneaza obiectul dorit.
* Visitor: Avand un obiect care poate fi de mai multe tipuri (implementari ale unei interfete), am ales visitor design pattern pentru a separa mai multe actiuni pe care vreau sa le realizez pe acest obiect, visitorii concreti fiind implementati astfel sa stie ce sa faca in cadrul fiecarui tip de obiect posibil. In acest mod am separat actiunile si m-am asigurat ca acestea se intampla pentru orice tip de obiect. De asemenea am folosit un visitor pentru a determina tipul de pagina, pentru a evita folosirea lui instanceOf.
* Singleton: (mai sus am spus ce tip si cum este exact implementat). Aici scopul este unul evident, in program trebuie sa fie o singura care ruleaza si pentru a ma asigura ca nu se incalca acest lucru am folosit singleton design pattern.
* Command: Am folosit acest design pattern la crearea si pornirea comenzilor pentru a introduce un intermediar care sa se ocupe de actiunea efectiva atunci cand imi doresc sa se intample asta in program. Astfel, prima data creez comanda si ulterior invoker-ul o porneste.

## Noutati Flow:

* Wrapped:
  * Pentru user am stocat separat entitatile frecventate si numarul de ascultari ale acestora. Acest wrapped se modifica de fiecare data cand playerul userului isi schimba starea;
  * Avand in vedere prezenta unor oarecum perechi in wrapped (entitati->lista ascultari), pentru a le sorta am creat o clasa interna generica, privata, care sorteaza entitatile in functie de ascultari. Aceasta este folosita pentru fiecare tip de entitate;
  * Am folosit genericitatea pentru claritatea codului, performanta si evitarea constructiei unor metode asemanatoare;
* Statistici monetizare:
  * Aici am ales sa folosesc un fel de cont pentru fiecare artist/host, unde sa adaug treptat toate revenue-urile;
  * Pentru usurinta am adaugat si un revenue fiecarei melodii, asta pentru a sustrage mai usor cea mai profitabila melodie;
* Premium:
  * Din acest punct de vedere nu apar mari diferente, doar in metoda care actualizeaza instantele ce acumuleaza informatii in legatura cu statusul playerului (ex: wrapped), se asigura ca in cazul in care utilizatorul este premium, sa se realizeze si monetizarea. (addSongForUser este metoda);
* Notifications:
  * Pentru notificari am realizat o lista pentru fiecare utilizator unde se stocheaza notificarile pe masura ce se realizeaza anumite comenzi;
  * In momentul unei afisari ale notificarilor se reseteaza lista;
* Recomandari si pagini:
  * Am facut schimbarile necesare la changePage pentru a putea sa acceseze si pagina unui artist/host cu ajutorul acestei comenzi;
  * Pentru a putea merge forward/backwards prin istoricul de pagini am folosit o lista de pagini, cu referinte la interfata Page, si un index care retine pozitia la care ne aflam in acest istoric;
  * Lista (istoricul paginilor) va contine mereu de la prima pagina accesata pana la cea la care ne aflam acum.
  * In cazul in care se da un changePage drept comanda, toate paginile de dupa pagina curenta (la care s-ar putea da forward) vor fi sterse;
  * Avand aceasta implementare se poate da si forward/backwards, daca este posibil si comanda cere acest lucru;
  * Pentru a actualiza recomandarile am creat metode care in momentul in care se cere update creeaza aceste recomandari (daca este posibil) si le pune in Listele de recomandari a userului;
  * Aceste liste sunt stocate in cadrul userului deoarece in cadrul implementarii mele homePage-ul nu este constant, el este creat doar in momentul in care este nevoie si cu ce status exista in momentul respectiv;
