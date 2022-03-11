# Document-Processing-using-Map-Reduce

I implemented a parallel program in Java which receives a set of documents, evaluates the length of the words processed
and gets the documents in ascending order based on the length of the words and their frequency. Each word will have an
assigned value based on the number of letters. The value of a word is determined using a formula based on Fibonacci's
sequence. The rank of a document is calculated by adding the values of all the words in that document. Also, for each
document I find the word (or words) with the maximum length.
After the parsing process, I determine the number of letters for every existing word in a document, obtaining a list of pairs
{length, frequency}, where the frequency represents the number of times a word of that specific length appears in the document.
In order to parallelize the processing of the documents I am using Map-Reduce. Each document will be split into smaller fragments
which will be parallelly processed (Map), resulting a partial dictionary containg {length, frequency} pairs and a list of the
words which have the maximum length. The next step is represented by all the partial dictionaries put together (Reduce),
obtaining another dictionary for the entire document (not only for fragments). The same thing is done for the lists. For each
document a rank and the number of maximum lengh words will be calculated.
