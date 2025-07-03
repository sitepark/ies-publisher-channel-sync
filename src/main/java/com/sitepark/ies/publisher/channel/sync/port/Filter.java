package com.sitepark.ies.publisher.channel.sync.port;

import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;

/** The filter can be used to ignore files and directories in the file system. */
public interface Filter {

  Filter ACCEPT_ALL = name -> true;

  boolean accept(PublishedPath name);
}
