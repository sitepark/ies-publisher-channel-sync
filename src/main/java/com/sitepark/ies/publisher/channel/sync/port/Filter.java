package com.sitepark.ies.publisher.channel.sync.port;

import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;

/** The filter can be used to ignore files and directories in the file system. */
public interface Filter {

  public static final Filter ACCEPT_ALL =
      new Filter() {
        @Override
        public boolean accept(PublishedPath name) {
          return true;
        }
      };

  public boolean accept(PublishedPath name);
}
