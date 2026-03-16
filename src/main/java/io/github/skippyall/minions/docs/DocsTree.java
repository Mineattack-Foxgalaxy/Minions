package io.github.skippyall.minions.docs;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DocsTree {
    private final BranchElement root;
    private final Map<Identifier, DocElement> entries = new HashMap<>();

    public DocsTree(BranchElement root) {
        this.root = root;
        initEntries();
    }

    private void initEntries() {
        DocElement element = root.first();
        do {
            entries.put(element.id, element);
            element = element.next();
        } while (element != null);
    }

    public BranchElement getRoot() {
        return root;
    }

    public DocElement getElement(Identifier id) {
        return entries.get(id);
    }

    public static abstract sealed class Element {
        private BranchElement parent;

        public BranchElement getParent() {
            return parent;
        }

        void setParent(BranchElement parent) {
            this.parent = parent;
        }

        public DocElement next() {
            return parent.next(this);
        }

        public DocElement previous() {
            return parent.previous(this);
        }
    }

    public static final class DocElement extends Element {
        public static final Codec<DocElement> CODEC = Identifier.CODEC.xmap(DocElement::new, DocElement::getId);

        private final Identifier id;

        public DocElement(Identifier element) {
            this.id = element;
        }

        public Identifier getId() {
            return id;
        }
    }

    public static final class BranchElement extends Element {
        public static final Codec<BranchElement> CODEC = Codec.<BranchElement>recursive("DocsBranch", codec ->
                Codec.either(codec, DocElement.CODEC)
                        .xmap(
                                Either::unwrap,
                                element -> switch (element) {
                                    case BranchElement branch -> Either.left(branch);
                                    case DocElement doc -> Either.right(doc);
                                }
                        ).listOf()
                        .xmap(BranchElement::new, branch -> branch.e)
        ).xmap(BranchElement::setParents, Function.identity());

        private final List<Element> e;

        public BranchElement(List<Element> elements) {
            e = elements;
        }

        public DocElement first() {
            return switch (e.getFirst()) {
                case BranchElement branch -> branch.first();
                case DocElement doc -> doc;
            };
        }

        public DocElement last() {
            return switch (e.getLast()) {
                case BranchElement branch -> branch.last();
                case DocElement doc -> doc;
            };
        }

        public DocElement next(Element current) {
            int nextIndex = e.indexOf(current) + 1;
            if(nextIndex < e.size()) {
                return switch (e.get(nextIndex)) {
                    case BranchElement branch -> branch.first();
                    case DocElement doc -> doc;
                };
            } else {
                if(getParent() != null) {
                    return getParent().next(this);
                } else {
                    return null;
                }
            }
        }

        public DocElement previous(Element current) {
            int previousIndex = e.indexOf(current) - 1;
            if(previousIndex >= 0) {
                return switch (e.get(previousIndex)) {
                    case BranchElement branch -> branch.first();
                    case DocElement doc -> doc;
                };
            } else {
                if(getParent() != null) {
                    return getParent().previous(this);
                } else {
                    return null;
                }
            }
        }

        private BranchElement setParents() {
            for(Element element : e) {
                element.setParent(this);
                if(element instanceof BranchElement branch) {
                    branch.setParents();
                }
            }
            return this;
        }
    }
}
