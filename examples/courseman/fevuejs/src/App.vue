<template src="./layouts/header.html"></template>
<script>
import "bootstrap/dist/js/bootstrap.bundle.js";
import "bootstrap/js/dist/popover.js";
import "bootstrap/dist/css/bootstrap.min.css";

import NestedNav from "./lib/NestedNav.vue";
import { store } from "./constants/store";
// console.log(store.form_tree)
export default {
  name: "App",
  data() {
    return {
      store: store,
      searchQuery: "",
    };
  },

  components: {
    NestedNav,
  },

  methods: {
    extractFirstSubfolder() {},

    formatStringToLowerCamelCase(inputString) {
      // Split the string into words using hyphen as the delimiter
      var words = inputString.split("-");

      // Capitalize each word
      var capitalizedWords = words.map(function (word) {
        return word.charAt(0).toUpperCase() + word.slice(1);
      });

      // Join the capitalized words without hyphens and return the result
      return capitalizedWords.join("");
    },

    getCurrentModuleName() {
      var currentModuleName = "";
      // Get the current URL from window.location
      var currentUrl = window.location.href;

      // Create a new URL object
      var parsedUrl = new URL(currentUrl);

      // Get the pathname from the URL
      var path = parsedUrl.pathname;

      // Split the path into segments using "/"
      var pathSegments = path.split("/").filter(function (segment) {
        return segment.length > 0; // Remove any empty segments
      });

      // Return the first segment (subfolder)
      if (pathSegments.length >= 1) {
        currentModuleName = pathSegments[0];
      }
      return currentModuleName
    },
  },
  computed: {
    currentModuleName() {
      var currentModuleName = "";
      // Get the current URL from window.location
      var currentUrl = window.location.href;

      // Create a new URL object
      var parsedUrl = new URL(currentUrl);

      // Get the pathname from the URL
      var path = parsedUrl.pathname;

      // Split the path into segments using "/"
      var pathSegments = path.split("/").filter(function (segment) {
        return segment.length > 0; // Remove any empty segments
      });

      // Return the first segment (subfolder)
      if (pathSegments.length >= 1) {
        currentModuleName = pathSegments[0];
      }
      let moduleName = this.formatStringToLowerCamelCase(currentModuleName);
      console.log("module name from url", moduleName);
      return moduleName
    },

    viewTree() {
      for (var i = 0, trees = store.form_trees; i < trees.length; i++) {
        if (trees[i].name === this.currentModuleName) {
          console.log("Navigation tree", trees[i]);
          return trees[i].children;
        }
      }
      return []
    },
  },
};
</script>
