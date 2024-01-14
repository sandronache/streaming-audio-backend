# Read me - etapa 3
### Andronache Sebastian George 323CD

## Read Me etapa 2:
(am ales sa las si aceste informatii in Read Me-ul etapei 3 pentru ca voi continua explicatiile plecand de la ce am spus aici)

## Design
* Pages:
  * Pentru pagini am gandit un design care sa ma ajute si pe viitor, intr-o maniera in care sa pot adauga si alte pagini.
  * Astfel am creat interfata Page care este implementata momentan de urmatoarele 4 pagini: HomePage, LikedContentPage, Artist, Host.
  * Pentru artist si host nu am creat o clasa separata, si implicit un obiect, deoarece elementele continute de o pagina sunt incluse in totalitate in informatiile artistului/hostului, iar folosind insasi obiectul artistului/hostului, care este o referinta, am asigurat ca la orice moment de timp pagina pe care se afla user-ul normal, in caz de este pe pagina unui artist/host, este la zi cu toate modificarile. 
  * Pentru a separa comportamente diferite in cadrul programului am folosit design pattern-ul Visitor. Astfel am creat un Visitor, care este acceptat ca visitor de catre interfata Page. Acest visitor este implementat in doua feluri:
    * Prima implementare concreta, UpdateVisitor, are rolul ca atunci cand se cere printarea paginii, inainte sa actualizeze la zi pagina. Avand in vedere implementarea folosind insasi artistii si hostii, in cazul acestora nu se schimba nimic, dar in viitor daca va trebui sa fie si ei modificati, din punct de vedere al implementarii va fi usor.
    * Cea de a doua, DisplayVisitor se ocupa de afisarea in functie de pagina si in functie de formatul impus.
* Users:
  * La aceasta etapa s-au mai adaugat 2 useri, artist si host, motiv pentru care am creat o interfata UserDatabase care sa fie implementata de catre toti cei 3 useri. De asemenea am folosit si un Factory care are rolul de a crea un user in functie de tipul oferit.
* Comenzi:
  * Am realizat si modificari cu privinta la interfetele/clasele abstracte in care sunt incluse comenzile. Atfel, am creat o interfata Commands care are o functie  startCommand. Aceasta interfata este implementata de toate comenzile.
  * Avem si o clasa abstracta, PlayerRelatedCommands care implementeaza interfata Commands. Aceasta adauga cateva metode in plus: getCurrentPlayer, updatePlayer si este extinsa de toate comenzile care  aplica modificari unui player sau tuturor. Am ales acest design pentru a nu repeta cod si pentru a folosi mostenirea in scopul punerii in comun a unei functionalitati comune. Fiind vorba doar de functionalitate am ales ca, clasa sa fie abstracta.



## Flow
* Programul incepe prin a crea aplicatia, oferindu-i library-ul de input, comenzile si array-ul de outputs.
* Aplicatia foloseste un Eager Singleton prin care o instanta este creata la rularea programului. Avand constructorul private se asigura ca doar acea instanta va fi folosita de-a lungul programului. Am ales Eager Singleton deoarece aplicatia este punctul in sine de plecare deci instanta ei se va folosi rapid.
* In continuare, programul instantiaza biblioteca interna a aplicatiei cu informatiile oferite la input, care ulterior vor fi folosite si modificate.
* Dupa constructia aplicatiei aplicatia porneste si comenzile sunt preluate pe rand si executate.
* Majoritatea implementarii din cadrul primei etape ramane neschimbata, in schimb am adaugat urmatoarele (semnificative):
  * Player-ul poate acum sa dea play si la un album, asadar am copiat comportamentul playlistului si l-am pus si pentru un album;
  * Search si select poate face acum si de artist/host/album;
  * In cazul in care se selecteaza un artist/host se seteaza noua pagina a utilizatorului normal si programul sterge selectia;
  * Cele mai semnificative comenzi din etapa 2 sunt stergerea unui album/ a unui user, despre care nu o sa detaliez deoarece am pus in cod comentariu cu detalii in legatura cu abordarea la fiecare caz tratat;


## ++ Resurse
* Am folosit inteligenta artificiala pentru a genera diferite comparatoare si functii de verificare, precum:

"Comparator<Integer> comparator = (index1, index2) -> {
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int likesComparison = Integer.compare(
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;likesPerArtist.get(index2), likesPerArtist.get(index1));
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (likesComparison == 0) {
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return allArtist.get(index1).getUsername().compareTo(
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;allArtist.get(index2).getUsername());
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return likesComparison;
<br>
};"
<br>
<br>
Comparator<Integer> comparator = (index1, index2) -> {
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int likesComparison = Integer.compare(likesPerAlbum.get(index2),
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;likesPerAlbum.get(index1));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (likesComparison == 0) {
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return allAlbums.get(index1).getName().compareTo(allAlbums.get(index2).getName());
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return likesComparison;
<br>
};"
<br>
<br>
"private boolean isValidDate() {
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (date.matches("\\d{2}-\\d{2}-\\d{4}")) {
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int day = Integer.parseInt(date.substring(0, 2));
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int month = Integer.parseInt(date.substring(THREE, 5));
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int year = Integer.parseInt(date.substring(6, 10));
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return (month <= 12 && day <= 31
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&& (month != 2 || day <= 28)
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; year >= 1900 && year <= 2023);
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return false;
<br>
}"
<br>

## Adaugari etapa 3:

# Motivatie Design:
* Factory: Am folosit factory design pattern la crearea userilor(mai multe detalii despre cum am facut asta se afla mai sus). Am folosit acest pattern pentru a nu ma ingrijora cu realizarea obiectelor de care am nevoie, factory-ul primeste tipul de obiect pe care il vreau si acesta imi returneaza obiectul dorit.
* Visitor: Avand un obiect care poate fi de mai multe tipuri (implementari ale unei interfete), am ales visitor design pattern pentru a separa mai multe actiuni pe care vreau sa le realizez pe acest obiect, visitorii concreti fiind implementati astfel sa stie ce sa faca in cadrul fiecarui tip de obiect posibil. In acest mod am separat actiunile si m-am asigurat ca acestea se intampla pentru orice tip de obiect. De asemenea am folosit un visitor pentru a determina tipul de pagina, pentru a evita folosirea lui instanceOf.
* Singleton: (mai sus am spus ce tip si cum este exact implementat). Aici scopul este unul evident, in program trebuie sa fie o singura care ruleaza si pentru a ma asigura ca nu se incalca acest lucru am folosit singleton design pattern.
* Command: Am folosit acest design pattern la crearea si pornirea comenzilor pentru a introduce un intermediar care sa se ocupe de actiunea efectiva atunci cand imi doresc sa se intample asta in program. Astfel, prima data creez comanda si ulterior invoker-ul o porneste.

# Noutati Flow:
* Wrapped:
   * Pentru user am stocat separat entitatile frecventate si numarul de ascultari ale acestora. Acest wrapped se modifica de fiecare data cand player-ul user-ului isi schimba starea
   * Avand in vedere prezenta unor oarecum perechi in wrapped (entitati->lista ascultari), pentru a le sorta am creat o clasa interna generica, privata, care sorteaza entitatile in functie de ascultari. Aceasta este folosita pentru fiecare tip de entitate
   * Am folosit genericitatea pentru claritatea codului, performanta si evitarea constructiei unor metode asemanatoare
* Statistici monetizare:
    * Aici am ales sa folosesc un fel de cont pentru fiecare artist/host, unde sa adaug treptat toate revenue-urile
    * Pentru usurinta am adaugat si un revenue fiecare melodii, asta pentru a sustrage mai usor cea mai profitabila melodie
* Premium:
    * Din acest punct de vedere nu apar mari diferente, doar in metoda care actualizeaza instantele ce acumuleaza informatii in legatura cu statusul player-ului (ex: wrapped), se asigura ca in cazul in care utilizatorul este premium, sa se realizeze si monetizarea. (addSongForUser este metoda)
* Notifications:
    * Pentru notificari am realizat o lista pentru fiecare utilizator unde se stocheaza notificarile pe masura ce se realizeaza anumite comenzi
    * In momentul unei afisari ale notificarilor se reseteaza lista
* Recomandari si pagini:
    * Am facut schimbarile necesare la change Page pentru a putea sa acceseze si pagina unui artist/host cu ajutorul acestei comenzi
    * Pentru a putea merge forward/backwards prin istoricul de pagini am folosit o lista de pagini, cu referinte la interfata Page, si un index care retine pozitia la care ne aflam in acest istoric
    * Lista (istoricul paginilor) va contine mereu de la prima pagina accesata pana la cea la care ne aflam acum.
    * In cazul in care se da un changePage drept comanda, toate paginile de dupa pagina curenta (la care s-ar putea da forward) vor fi sterse.
    * Avand aceasta implementare se poate da si forward/backwards, daca este posibil si comanda cere acest lucru
    * Pentru a actualiza recomandarile am creat metode care in momentul in care se cere update creeaza aceste recomandari (daca este posibil) si le pune in Listele de recomandari a user-ului.
    * Aceste liste sunt stocate in cadrul user-ului deoarece in cadrul implementarii mele homePage-ul nu este constant, el este creat doar in momentul in care este nevoie si cu ce status exista in momentul respectiv.

# ++ Resurse:
* Am folosit inteligenta artificiala pentru a genera diferite comparatoare si functii de verificare, precum:
   * Construirea comparatoarelor:
     Collections.sort(elPairs, Comparator.<ELPair<T>, Integer>comparing(ELPair::getListeners)
     .reversed()
     .thenComparing(ELPair::getEntity, customComp)); // customComp este o atributa din entitate dupa care se realizeaza sortarea

