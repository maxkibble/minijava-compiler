# minijava-compiler
This is a class project for undergraduates of Peking University.

It transform minijava code (a subset of Java syntax) to mips assembly code which can be excecuted.

## Install
Using your Java IDE to import cloned code to a new project. (Do NOT specify directory tests/ to test module in your project, treat them as simple example minijava code)

Set your JDK (1.7 or higher) and language level (7 or higher)

Specify compiler output path, most Java IDE will automatically do this (create out/ directory in the root directory)

Build your project, if it builds successfully, the installation is finished.

## Usage
### typecheck: 
run src/minijava/typecheck/Main.java
### assembly code generation: 
run src/Main.java

You can choose to generate different intermediate code by specifying different Main functions under src/ directory.

There are 3 types of intermediate code: *.pg, *.spg, *.kg (In generation order). They represent different phases of compilation.

For more implementation details, you can read the pdf file in report/ directory.
