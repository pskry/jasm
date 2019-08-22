# jASM
The _jASM_ project aims to provide a stable assembly language
definition for the Java Virtual Machine (JVM) class file format,
as well as a reference implementation thereof.

This project is heavily inspired by the output of the javap program,
the ARM GNU assembler as well as the obvious Jasmin project started by
Jonathan Meyer over more than a decade ago.

## Getting Started
The following section will provide you with a copy of the project up
and running on your local machine for development and testing purposes.

See section __Deploying jASM__ for notes on how to install and deploy
the project on a live system.

### Prerequisites
This project is written in pure Java 12 and therefore only has very few
prerequisites to install before you can get started.

Even the build-tool is bundled and will download it's dependencies
automatically. You only need to provide an active internet connection.

| Dependency | Version | Description                    | Type    |
| ---------- | ------- | ------------------------------ | ------- |
| JRE        | 12      | Java Runtime Environment (JVM) | `run`   |
| JDK        | 12      | Java Development Kit           | `build` |

> Dependency types are categorized as either `build` or `run`,
> where the former is only needed while building/compiling the project.
> Whereas the latter is needed to actually run the compiled program.

### Building jASM
> Building _jASM_ is as easy as 1-2-3.

To automatically download all necessary dependencies, compile and
package _jASM_ you only need to run the following command in the
_root directory_ of the project (where this readme file is located).
```bash
$ ./mvnw clean package
```

If you wish to build without running tests, please run the following
command instead.
```bash
$ ./mvnw clean package -DskipTests=true
```

The above command will compile and package _jASM_ as a `.zip` as well
as `.tar.gz` file that can be found in the `target` directory.

The archives have the following structure:
```
.
`-- jasm-<version>-dist.tar.gz
    |-- LICENSE
    |-- README.md
    |-- bin
    |   |-- jasm        (assembler    *nix    runscript)
    |   |-- jasm.bat    (assembler    windows runscript)
    |   |-- jdsm        (disassembler *nix    runscript)
    |   `-- jdsm.bat    (disassembler windows runscript)
    `-- lib
        `-- ...         (runtime libraries)
```

## Running tests and static code analysis
This project makes extensive use of automated testing as well as employ
rigorous static code analysis rules.

To run just the test-suite without static code analysis, please run the
following command in the `root` directory of the project:
```bash
$ ./mvnw test -B
```

To run the whole test-suite including static code analysis, code-style
and license-formatting inspections, please execute the following
command instead:
```bash
$ ./mvnw test -B -Panalyze
```

## Deploying jASM
> The following commands might need super-user rights to execute.
> If so, please prepend `sudo`.

In order to deploy _jASM_ you only need to extract the aforementioned
distribution container (`.tar.gz` or `.zip` file).
The following command will extract the tarball into a directory named
`jasm-<version>` located within the present working directory.
```bash
$ tar -xzf jasm-<version>-dist.tar.gz
```

From there, you can copy/move the whole folder into a shared location,
i.e. `/opt`.
```bash
$ mv jasm-<version> /opt
```

Optionally, you may also want to add a symlink for convenience.
```bash
$ ln -s /opt/jasm-<version>/bin/jasm /usr/bin/jasm
$ ln -s /opt/jasm-<version>/bin/jdsm /usr/bin/jdsm
```

You are all done. You can now use _jASM_ from anywhere by just typing
in the command `jasm` in your terminal.

## Contributing
Since this project is still in it's infancy, we have not figured out
yet how to exactly deal with contributions at this stage.

Nevertheless please feel free to contact us when you think this project
is interesting and you want to get involved.

## Versioning
We use [SemVer](http://semver.org/) for versioning. For the versions
available, see the
[tags on this repository](https://gitlab.com/baeda/jasm/-/tags).

## Authors
* Peter Skrypalle - _Initial work_ - [baeda](https://gitlab.com/baeda)

## License
This project is licensed under the Apache License Version 2.0 - see the
[LICENSE](LICENSE) file for details.

## Acknowledgments
* [Jasmin](http://jasmin.sourceforge.net/)
* [A template to make good README.md](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2)
