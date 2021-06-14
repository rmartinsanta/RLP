# Solving the Regenerator Location Problem with an Iterated Greedy approach

The evolution of digital communications has resulted in new services that require from secure and robust connections. Nowadays, a signal must be transmitted to distant nodes, and the quality of the signal deteriorates as the distancebetween the endpoints increases. Regenerators are special components that are able to restore the signal, in order to increase the distance that the signal can travel without losing quality. These special components are very expensive to deploy and maintain and, for this reason, it is desirable to deploy the minimum number of regenerators in a network. We propose a metaheuristic algorithm based on the Iterated Greedy methodology to tackle the Regenerator Location Problem, whose objective is to minimize the number of regenerators required in a network. The extensive computational experiments show the performance of the proposed method compared with the best previous algorithm found in the state of the art.


## Authors
Juan David Quintana Pérez
Raúl Martín Santamaría
Jesus Sánchez-Oro Calvo
Abraham Duarte Muñoz

## Datasets

Instances are categorized in different datasets inside the 'resources/rlp-instance' folder. 

## Code and executable

You can just run the RLP.jar as follows.

```
java -jar RLP.jar
```

To solve new instances add them to the `resources/rlp-instance/very_large` folder or create a new folder inside `resources/rlp-instance` and edit `src/grafo/rlp/data/RLPDataSetManager.java.` and Main.

After the program finishes, `Solution` folder will contain output per instance and `experiments` folder will have all results grouped in a Microsoft Excel file.


## Cite

Consider citing our paper if used in your own work:
(Pending)

