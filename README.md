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
[AGENTS.md](doc/AGENTS.md) or use the available ones, *MemoryAgent()* and
*RedisAgent(String redisUri)*, where the former saves the idempotence metadata
on memory, and the latter saves it on a Redis service.

**Notes**: All the annotation's parameters are optional, defaulting to the values
shown above. `include` and `exclude` cannot be used together, and
*ParameterFilterException* if is raised at runtime if it occurs. If an invalid
value is defined on `hash`, *HashingStrategyException* is raised. Currently, the
only valid values for `hash` are `hashCode` and `toString`.

### Example

Suppose you want to assure idempotence when sending e-mail to an user, and consider
that `User.toHashCode()` is a great hash function that can distinguish the users.
This way, you can use the `@Idempotent` annotation including just the user with its
hash function, to differentiate among method calls. The code will be this way:

```java
@Service
public class EmailService {
    
    @Idempotent(include = "user", hash = "hashCode")
    public String send(User user, String emailHeader, String emailBody) {
        // logic here to send email
        // ...
        return body;
    }
}
```

Then you create a bean with the existing *IdempotenceAgent* as beans (as in the
example below, where a *RedisAgent* is created using a `@Configuration` class),

```java
@Configuration
public class IdempotenceConfiguration {
    
    @Bean
    public IdempotenceAgent createAgent() {
        return new RedisAgent("redis://localhost:6379");
    }
}
```

or create your bean implementing *IdempotenceAgent* interface
(see [AGENTS.md](doc/AGENTS.md)).

### Global configuration

Hash strategy, ttl, and also if events of idempotence must be logged, can be
globally configured on _application.properties_, defining `hash`, `ttl`
and `logging`, respectively, on `vitorcezli.spring-ext.idempotence`. For
example, if you want to globally assure idempotence for one minute and
`toString` as the default hash strategy, also logging the events of idempotence,
you must insert the following lines on your _application.properties_:

```
vitorcezli.spring-ext.idempotence.hash=toString
vitorcezli.spring-ext.idempotence.ttl=60
vitorcezli.spring-ext.idempotence.logging=true
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
    <version>1.0.0</version>
</dependency>
```

For Gradle, insert the following on *gradle.file*:

### Contribution

If you want to contribute to this project, please refer to
[CONTRIBUTING.md](doc/CONTRIBUTING.md).