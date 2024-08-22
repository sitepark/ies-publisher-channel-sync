# Publisher Channel Sync

The published content is written as files from the IES to a directory (Publisher Channel). They are also deleted by the CMS when the publication is canceled. The Publisher Channel Sync can be used to check whether the files in the directory correspond to the publication status of the IES. If this is not the case, the corresponding data in the directory can be synchronized so that they correspond to the publication status of the IES.

## Usage

This package is a core package and requires adapter implementations for the following ports:

- `com.sitepark.ies.publisher.channel.sync.port.Filter`: The filter can be used to ignore files and directories in the file system. A default implementation `com.sitepark.ies.publisher.channel.sync.port.Filter.ACCEPT_ALL` is available for this purpose, which accepts all files and directories.
- `com.sitepark.ies.publisher.channel.sync.port.Hasher`: Must be implemented to calculate the hash value of a file. The same algorithm used by the IES must be used here.
- `com.sitepark.ies.publisher.channel.sync.port.Publisher`: Must be implemented to determine the publications and to create and delete publications.
- `com.sitepark.ies.publisher.channel.sync.port.SyncNotifier`: Must be implemented in order to be able to output the sync outputs.

In order to synchronize a publication channel, it is first necessary to analyse the differences between the file system and the publications in the IES. The `Analyse`-UseCase is used for this purpose.

This first requires the publication channel, which can be created via a file system path within a channel.

```java
Path path = Path.of("path/within/a/channel");
ChannelFactory channelFactory = new ChannelFactory();

Channel channel = channelFactory.create(path);
Pubblisher publisher = new PublisherAdapter();
Hasher hasher = publisherAdapter.createHasher();

Analyse analyseUseCase = new Analyse(channel, publisher, hasher);
```

A relative path is required for the analysis, from which the analysis should begin. Here, `path` can be used, which is converted into a relative path via the `channel` instance.

```java
Path base = channel.toPublicationPath(path);
```

Damit kann die Analyse gestartet werden.

```java
boolean recursive = true;
AnalyserResult result = analyseUseCase.analyse(base, recursive);
```

The differences between the file system and the publications in the IES can be queried via `result.entries()`. The `AnalyserResult` instance contains a list of `ResultEntry` instances that describe the differences between the file system and the publications in the IES.

Mit Hilfe von `result`kann über den `Syncronize`-UseCase die Synchronisation gestartet werden.

```java
SyncNotifier notifier = new SyncNotifierAdapter();

boolean test = false:
boolean deleteForce = true;
boolean showCollisions = true;

Syncronize syncronizeUseCase = new Syncronize(
    test,
    deleteForce,
    showCollisions,
    publisher,
    notifier);
syncronizeUseCase.syncronize(result);
```

If `test` is set to `true`, synchronization is carried out in test mode. No changes are made to the files in this mode.

Only if `deleteForce` is set to `true`, entire directories are also deleted recursively, otherwise only files are deleted

If `showCollisions` is set to `true`, collisions are also reported via the `notifier`.

## Implementation Details

The analysis is carried out in two phases. First, the directory to be analyzed is scanned recursively if necessary. This involves searching for publications that match the current directory and analyzing whether the file system matches the publications. There may be various cases of differences. Each case is implemented in a separate `PublishedPathAnalyser`. An analysis can detect an error or determine that everything is in order. In addition, each analysis can decide whether further analyses should be carried out afterwards.

In the second phase, all publications are analyzed in order to determine, for example, whether there are publications that are not in the file system. There may also be various cases of differences. Each case is implemented in a separate 'PublicationAnalyser'. An analysis can detect an error or determine that everything is in order. In addition, each analysis can decide whether further analyses should be carried out afterwards.

The result of the analysis is summarized in an `AnalyserResult`. This contains a list of `ResultEntry` instances that describe the differences between the file system and the publications in the IES.

During synchronization, the differences between the file system and the publications in the IES are processed. The `ResultEntry` instances provide a result type that describes the type of error. There is a separate syncronizer implementation for each result type. This can correct the error.
