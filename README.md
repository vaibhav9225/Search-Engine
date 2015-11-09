# Search-Engine
A speedy and flexible search engine for indexing data and providing highly relevant search results.

This search engine consists of the following main components:
- Parser: Parses the raw text file to create document objects with properties such as Title, Body, etc.
- Analyzer: It tokenizes the document objects to pass it through a series of analyzers to filter out irrelevant data and transform the remaining data to make it ready for indexing.
- Indexer: It reads the data word by word from the analyzer pipeline to create an index of words and poining to the list of document Ids they are present in along with other meta data such as the numbre of occurances of word in the document, etc.
- Query Parser: It reads the user query, fetches data from index and user IR models to decide the relavancy of the results to display them to the data.
