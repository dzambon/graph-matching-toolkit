# Graph Matching Toolkit 

**Author:** Riesen Kaspar 

---

## Quick Start

We recommend the following three steps:

#### STEP 1:
Unzip the archive `Sources.zip`, integrate the whole framework in an IDE (e.g. Eclipse) and build the project. 

#### STEP 2:
Define a properties file in order to define the parameters of your graph matching task. In the folder `properties` you find three examples of such properties (for more details on these parameters we refer to our paper: 

*K. Riesen, S. Emmenegger and H. Bunke. A Novel Software Toolkit for Graph Edit Distance Computation.. In W.G. Kropatsch et al., editors, Proc. 9th Int. Workshop on Graph Based Representations in Pattern Recognition, LNCS 7877, 142–151, 2013.*

#### STEP 3:
Run the graph matching as a java application. The main method is in `GraphMatching.java`, the sole program argument is an URL pointing to the properties file (e.g.: `./properties/properties_molecules.prop`)

---

## Change Log

Changes to the original code.

#### 1.1.0 (2017-10-05) `b16d7f4`
* More concise console output.
* Exception handling: easier debug of issues related to parsing the properties files.
* More details in the result file.
* More comments about how to set the properties in `properties_letter.prop`.

#### 1.0.1 (2017-06-27) `0b51e0f`
* Implemented the cost function for `csvDouble` for attributes in the form `[double, double, ... , double]`.

#### 1.0.0 (2017-01-30) `e833baa`
* Version provided to me by Kaspar Riesen.

---

### Discalimer

The author of the source code is Kaspar Riesen. 
Daniele Zambon ([dan-zam](https://github.com/dan-zam)) is only the owner of the github repository.
