# [subman.io](http://subman.io/)

Service for fast subtitle searching.

[Chrome extension sources.](https://github.com/subman/subman-chrome)

[Web application sources.](https://github.com/subman/subman-web)

[Parser sources.](https://github.com/subman/subman-parser)

## Api

For search send GET request like:

    http://subman.io/api/search/?query=file-name
    
Available params:

* `lang` &mdash; language, by default used `english`;
* `source` &mdash; source id, by default used `-1` (equals `all`);
* `limit` &mdash; limit of result, by default used `100`;
* `offset` &mdash; result offset, by default used `0`.

For bulk search send POST request to `http://subman.io/api/bulk-search/` with transit-encoded body with:

* `queries` &mdash; list of queries to search;
* `source` &mdash; source id, by default used `-1` (equals `all`);
* `limit` &mdash; limit of result, by default used `100`;
* `offset` &mdash; result offset, by default used `0`.
    
All languages with subtitles count available in:

    http://subman.io/api/list-languages/

All sources with names available in:

    http://subman.io/api/list-sources/

You can get total subtitles count in:

    http://subman.io/api/count/

For decoding api response you should use [transit](https://github.com/cognitect/transit-format).

## Deploy

For running:

```bash
docker-compose up web parser nginx
```

## Major migrations

### 2015-01-10 Presentation db support

In separate shells run:

```bash
docker-compose run maintain
```

And execute in REPL:

```clojure
(from-index-to-raw-db!)
```
