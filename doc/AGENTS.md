## Agents

The idempotence is assured by using agents that communicate with the memory, or a
service, to save the functions that were executed with its arguments. These
agents implement the _IdempotenceAgent_ interface, whose functions are described
below:

* `void save(final String hash, final byte[] payload, final int ttl)`: It must
  be implemented by the idempotence agent to save the object returned by the
  execution of a function which has the `@Idempotent` annotation.
  `final String hash` is a hash that differentiates functions and arguments, which
  is generated after a hash strategy is applied. `final byte[] payload` is object
  returned after the function execution on its serialized form; and `final int ttl`
  is a value that represents how many seconds the idempotent agent must assure
  idempotence of a function;
* `byte[] read(final String hash)`: This function must return the object generated
  after the execution of a function with the `@Idempotent` annotation (it must
  be the same `byte[]` content passed to the `void save` function above). `null`
  must be returned for functions that were not executed, or if `ttl` passed on
  `void save` has expired.
  
If you need an agent that is not contemplated on this code, you can create one
implementing the *IdempotenceAgent* interface. Refer to
[RedisAgent](https://github.com/vitorcezli/springext/blob/main/src/main/java/io/github/vitorcezli/idempotence/agents/redis/RedisAgent.java)
for an example.