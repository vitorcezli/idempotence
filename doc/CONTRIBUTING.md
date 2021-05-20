## Contributing

There are three ways of contributing to this code, which are creating new
agents, developing new hash strategies, and reporting bugs.

### Creation of new agents

Currently, the code just has two agents: _MemoryAgent_ and _RedisAgent_.
You can create a new agent following the guideline on [AGENTS.md](AGENTS.md)
and submit a PR to this repository. The agent must be developed on *idempotence/agents*,
creating a folder with the same name as the agent and tested on the *test*
folder.

### Development of new hash strategies

The strategies that use `toString()` and `hashCode()` functions of the objects
are used to differentiate the arguments that are passed to the idempotent
functions and assure idempotence. You can create a new strategy and submit it
to this repository.

To this end, perform the following steps:

1. Create a new class under *idempotence/hash/implementations* folder with the
   name of the new hash strategy;
2. Make the class extend _HashingStrategy_ and implement its abstract function:
`String calculateHashObjects(final Object[] objects)`. The list of objects is
   the arguments passed to the idempotent functions, and the hash strategy must
   be executed on these objects;
3. Define a string for the new strategy on the `Map<String, HashingStrategy> strategies`
   variable of _HashingStrategySelector_;
4. Test if the strategy is correctly returned on _HashingStrategySelectorTest_ on
   the *test* folder;
5. Add tests for the new strategy of hash.

### Bugs report

If you find a bug when using this package, create an issue with the details of the bug.

### Another way to contribute?

If you want to contribute to this project another way, create an issue, or email
vitorcezli@gmail.com.