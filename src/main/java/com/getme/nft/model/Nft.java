package com.getme.nft.model;

import java.util.List;

public record Nft(String imageLink,
                  String name,
                  List<String> details,
                  List<String> links,
                  String description,
                  List<String> tags) {
}
