[![](https://jitpack.io/v/sergeshustoff/diffadapter.svg)](https://jitpack.io/#sergeshustoff/diffadapter) 
# DiffAdapter
Simple adapter based on DiffUtil and view binding

## Installation
Add to your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Add the dependency:

	dependencies {
	        implementation 'com.github.sergeshustoff:diffadapter:1.0.0'
	}
  
## Usage

Make sure you are using view bindings (https://developer.android.com/topic/libraries/view-binding)

Create your adapter:

    private val adapter = diffAdapterBuilder()
        .forType<Item>() // define a type that will be bound to view holder under the hood
        .withId { it.id } // allows DiffUtil to identify same item when it moves or changes
        .includingSubtypes(ItemImpl::class) // define subtypes if needed. By default holder is bound to specific class, not to all it's subclasses
        .binding(
            inflateBinding = ItemBinding::inflate, // a view binding for item.xml
            init = {
                // init view if needed
            },
            bind = {
                // bind item to view
            },
            onClick = {
                // handle click
            },
            itemsCountInRow = 2 // show 2 items in a row (for GridLayoutManager)
        )
        .forType<Header>() // define another type
        .allInstancesAreSame() // every instance of this type will be considered the same item. Useful for headers/footers
        .binding(
            inflateBinding = ItemHeaderBinding::inflate,
            init = {
                // ...
            },
            bind = {
                // ...
            },
            itemsCountInRow = 1 // show 1 header in a row (for GridLayoutManager)
        )
        .build()
        
Set adapter to RecyclerView and fill it with data:
  
    view.searchResults.adapter = adapter

    // fill adapter with data. Make sure you don't use same instance of mutable list, otherwise DiffUtill won't work
    adapter.items = listOf(Header("Header text"), Item(1, "Item1"))
