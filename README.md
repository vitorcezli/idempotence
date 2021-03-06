Consider the call of a function as a tuple *<C, F, a_1, a_2, ..., a_n>*, where
*C* is the class, *F* is the function that is called, and *a_1, a_2, ..., a_n*
are the values obtained after a hash function is applied to its arguments (see
the next paragraph). The solution of this project makes the calls with the
same tuple be executed just once, thus assuring idempotence.

This solution can be used on **public methods of beans**, annotating the ones
that must be idempotent with `@Idempotent(hash = "hashCode", include = {},
exclude = {}, ttl = 0)`. `hash` defines the function *f* that is applied to
the arguments of the annotated function before the tuple is created. For example,
if `hash = "toString"`, `a_i = argument_1.toString()` for *1 < i < N*, where *N*
is the number of arguments on the function. The `include` and `exclude` define
the arguments that must be included or excluded on the tuple by specifying the
name of their parameters. Finally, `ttl` (time to live) defines the number of
seconds that idempotence must hold, and if `ttl <= 0`, idempotence is guaranteed
infinitely.

After using this annotation on the functions that must be idempotent,
*IdempotenceAgent* bean must be configured to define where the idempotence
metadata will be saved. You can create a bean as defined on
[AGENTS.md](doc/AGENTS.md) or use the available ones, `MemoryAgent()` and
`RedisAgent(String redisUri)`, where the former saves the idempotence metadata
on memory, and the latter saves it on a Redis service.

**Notes**:

- All the annotation's parameters are optional, defaulting to the values 
  shown above. `include` and `exclude` cannot be used together, and
  *ParameterFilterException* if is raised at runtime if it occurs. This
  exception is also raised if invalid parameters are specified on `include`
  and `exclude`;
- If an invalid value is defined on `hash`, *HashingStrategyException* is
  raised. Currently, the only valid values for `hash` are `hashCode` and
  `toString`;
- The functions annotated with `@Idempotent` must return void, or an object
  that implements the `Serializable` interface (all primitives implement this
  interface). The object of the first call is saved in the idempotence
  metadata and returned on the next calls, which are not executed to assure
  idempotence.

### Example

Suppose you want to have idempotence when sending e-mail to an user, and consider
that `User.toHashCode()` is a great hash function that can distinguish the users.
This way, you can use the `@Idempotent` annotation to include just the user, and
indicate to apply `toHashCode()` to differentiate among method calls:

```java
@Service
public class EmailService {
    
    @Idempotent(include = "user", hash = "hashCode")
    public void send(User user, String emailHeader, String emailBody) {
        // logic here to send email
    }
}
```

Then you can return a bean of the existing *IdempotenceAgent*s (as in the
example below, where a *RedisAgent* is returned using a `@Configuration` class),

```java
@Configuration
public class IdempotenceConfiguration {
    
    @Bean
    public IdempotenceAgent createAgent() {
        return new RedisAgent("redis://localhost:6379");
    }
}
```

or create your bean implementing the *IdempotenceAgent* interface
(see [AGENTS.md](doc/AGENTS.md)).

### Global configuration

Hash strategy, ttl, and also if events of idempotence must be logged, can be
globally configured on _application.properties_, defining `hash`, `ttl`
and `logging`, respectively, on `io.github.vitorcezli.idempotence`. For
example, if you want to globally assure idempotence for one minute and
`toString` as the default hash strategy, also logging the events of idempotence,
you must insert the following lines on your _application.properties_:

```
io.github.vitorcezli.idempotence.hash=toString
io.github.vitorcezli.idempotence.ttl=60
io.github.vitorcezli.idempotence.logging=true
```

**Notes**: Local definitions take precedence over global configuration. This
way, the global definitions of ttl and hash are overridden for the values
on the local `@Idempotent` annotation.

### Installation

If using Maven, insert the following dependency on your *pom.xml* file:

```xml
<dependency>
    <groupId>io.github.vitorcezli</groupId>
    <artifactId>idempotence</artifactId>
    <version>1.0.1</version>
</dependency>
```

For Gradle, verify if *gradle.file* is configured to read from Maven, by having
this section:

```gradle
repositories {
    mavenCentral()
}
```

Then, insert `implementation 'io.github.vitorcezli:idempotence:1.0.1'` on
`dependencies` section:

```gradle
dependencies {
    implementation 'io.github.vitorcezli:idempotence:1.0.1'
}
```

### Contribution

If you want to contribute to this project, please refer to
[CONTRIBUTING.md](doc/CONTRIBUTING.md).
