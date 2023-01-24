<div align = "center">
   <a href="https://github.com/pama- lee/nucleus">
     <img src = "./ img/nucleus 1@0.25x.png" alt = "logo" width = "100" height = "100">>
   </a>

<h3 align = "center"> nucleus </h3>

   <p align = "center">
     A plug -in Java framework
     <br />
   </p>

<details>
   <summary> Directory </Summary>
   <OL>
     <li>
       <a href="#about-the-project"> About this project </a>
     </li>
     <li>
       <a href="#getting-Started"> Start Work </a>
       <ul>
         <li> <a href="#Prejisites"> dependence </a> </li>
         <li> <a href="#Installation"> Create a plugin </a> </li>
       </ul>
     </li>
     <li> <a href="#Roadmap"> Plan to implement </a> </li>
     <li> <a href="#contributing"> contribution </a> </li>
     <li> <a href="#License"> Open Source Agreement </a> </li>
     <li> <a href="#Contact"> Contact us </a> </li>
     <li> <a href="#acknowledgments"> Xie </a> </li>
   </OL>
</Details>



## About this project

This is a upper -level framework based on SpringBoot, which is committed to realizing the instance of the SpringBoot service and realizing its hot insertion. Save development costs and deployment difficulties and costs. Help.

Why use:
* The main service is started once, and the plug -in is hot.
* Provide many interfaces and realize the control of the life cycle, which is convenient for injecting in various processes of Spring
* Easy to use, convenient development, and advocate programming development: Smile:

<p align = "right"> (<a href="#readme- top"> back to top </a>) </p>

## Start using

Plug -in development process

### Dependencies

* Maven
  ```
  <dependency>
  <Groupid> CN.DEV-SPACE </Groupid>
  <Arttifactid> Nucleus </Artifactid>
  <version> </version>
  </dependency>
  ```

### Create a plugin

1. Create ** NUCLEUS.yml ** configuration file in the project root directory

```yaml
### Nucleus.yml Example
Name: plug -in name (must)
Author: Author (must)
Version: version (must)
Main: com.example.main (starting class, must)
Description: Description (optional)
Route: Routing address (optional)
Language: The first choice language (optional, the language selected by the main frame)
```

<p align = "right"> (<a href="#readme- top"> back to top </a>) </p>

2. Start the class inheritance `CN.Devspace.nucleus.plugin.pluginbase`

```java
import cn.devspace.nucleus.plugin.pluginbase;
public class Main extends PluginBase {
}
```



## Plan implementation

- [X] Add update log
- [X] Improve the internal login app
- [] Multi -language support
    - [x] Chinese
    - [] English
    - [] French

<p align = "right"> (<a href="#readme- top"> back to top </a>) </p>



## How to contribute

If you have a suggestion that can make the situation better, please allocate the repository and create a driving request.
Don't forget to give the project a Star! Thanks again!

1. Fork this project
2. Create your own development branches (`git checkout -B feature/amazingfeature`)
3. Submit (`git commit -m 'add some amazingfeature'`)
4. Push to your branch (`Git Push Origin Feature/Amazingfeature`)
5. Open the merger request

<p align = "right"> (<a href="#readme- top"> back to top </a>) </p>




## License

Distitived undolingnse. See `livense.txt` for more information.

<p align = "right"> (<a href="#readme- top"> back to top </a>) </p>



## Contact us

Pama lee - pama@pamalee.cn

<p align = "right"> (<a href="#readme- top"> back to top </a>) </p>



## Acknowledgments

* Maven
* SpringBoot
* Java
* Hibernate
* MyBatis-Plus
* NUKKIT

<p align = "right"> (<a href="#readme- top"> back to top </a>) </p>
