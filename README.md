Insight Data Engineering - Coding Challenge Explaination - by Ling Ding
=======================================================================

1. External Java library needed to parse Json input file
   
   http://www.java2s.com/Code/JarDownload/json-simple/json-simple-1.1.1.jar.zip

2. Design Archetecture diagram
                                  _____________________            
                                 /                     \ 
                                 | Input Tweet.txt     | 
                                 \_____________________/
                                             | 
                                             | one line per iteration
                                   __________V__________                           
                                  /                     \  
                                  |                     |  
             one Tweet            |      Json File      |  
             at a time -----------|      Parser         |-------- 
                       |          |(Class TweetHashTag) |       |   
                       |          |                     |       |  
                       |          \_____________________/       |     
                       |                                        |                
                       |                                        | add new                 
                       |                                        | node and               
                       |                                        | edges                   
                       |                                        |               
                _______V_____________                  _________V__________                 
               /                     \               /                     \   
               |                     |  Tweets to be |                     |   
               |   Sliding Window    |  Evicted      | Tag Relationship    |   
               |   Tracker           |-------------->| Graph               |   
               |   (class Tweets)    |  remove nodes | (class Graph)       |   
               |                     |  and edges    |                     |   
               \_____________________/               \_____________________/   
                                                                |               
                                                                |               
                                                                |               
                                                                V               
                                                            Average Grade                    
                                                                                
                                                                                
                                                                                



