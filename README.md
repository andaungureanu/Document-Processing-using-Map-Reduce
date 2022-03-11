# Document-Processing-using-Map-Reduce
In functia main am in principiu doua obiective:
1. construiesc parametrii pentru task-urile map (MapWorker) si preiau rezultatele
date de acestea (MapTaskResult)
2. construiesc parametrii pentru task-urile reduce (ReduceWorker) si preiau rezultatele
date de acestea (ReduceTaskResult)

Parametrii primiti de MapWorker si ReduceWorker sunt cei precizati si aratati prin
exemple in cerinta temei. MapTaskResult are 3 campuri: map<lungime, numar aparitii>,
lista cu cuvintele de lungime maxima si numele fisierului. ReduceTaskResult are 4
campuri: numele fisierului, rank-ul, lungimea de cuvant maxima si numarul de cuvinte
ce au aceasta lungime.

In main imi construiesc un vector de offset-uri si unul de dimensiuni pentru task-urile
map si ii populez folosindu-ma de dimensiunile fisierelor rezultate din functia
Files.size. Indeplinesc obiectivele stabilite mai sus, iar la final prelucrez rezultatul
obtinut, urmand sa il afisez in fisierul de output. 

Atat metodele call din MapWorker si din ReduceWorker, cat si implementarea din main
sunt descrise prin comentarii in cod la fiecare pas mai important.
