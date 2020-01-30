from nexus.core.registry import Registry
from org.joml import Vector3f
from org.joml import Vector2f
from com.artemis import World
from nexus.core.math import Transform
from nexus.core.render import Billboard
from nexus.core.render import AnimationMap
from nexus.core.render import Bones
from nexus.core.registry.assets import MeshAsset
from nexus.core.registry.assets import ImageAsset
from nexus.core.registry.assets import AnimationAsset


class Scene:
    def __init__(self):
        self.registry = context.get(Registry)
        self.world = context.get(World)
        pass

    # Adds an animated entity to the scene
    def addAnimatedEntity(self, position, rotation, scale, model, image, animation):
        transform = self.createTransform(position, rotation, scale)
        mesh = self.registry.get(model, MeshAsset).toComponent()
        texture = self.registry.get(image, ImageAsset).toComponent()
        entity = self.world.create()
        animationMap = self.getAnimation(animation)
        self.world.edit(entity).add(animationMap)
        self.world.edit(entity).add(transform)
        self.world.edit(entity).add(mesh)
        self.world.edit(entity).add(Bones())
        self.world.edit(entity).add(texture)
        return entity

    # Adds a static entity to the scene
    def AddEntity(self, position, rotation, scale, model, image):
        transform = self.createTransform(position, rotation, scale)
        mesh = self.registry.get(model, MeshAsset).toComponent()
        texture = self.registry.get(image, ImageAsset).toComponent()
        entity = self.world.create()
        self.world.edit(entity).add(transform)
        self.world.edit(entity).add(mesh)
        self.world.edit(entity).add(texture)
        return entity

    # Plays the specific animation on the given entity
    def playAnimation(self, entity, animation):
        ent = self.world.getEntity(entity)
        animMap = ent.getComponent(AnimationMap)
        animMap.play(animation, 1.0)
        return

    @staticmethod
    def createTransform(position, rotation, scale):
        transform = Transform()
        transform.setPosition(position.x(), position.y(), position.z())
        transform.setScale(scale.x(), scale.y(), scale.z())
        transform.setRotation(rotation.x(), rotation.y(), rotation.z())
        return transform

    def addBillboard(self, entity, offset, color, size):
        billboard = Billboard()
        billboard.setColor(color)
        billboard.setSize(size)
        billboard.setOffset(offset)
        self.world.edit(entity).add(billboard)
        pass

    @staticmethod
    def getAnimationName(name):
        val = name.split(':')
        index = len(val) - 1
        return val[index]

    def getAnimation(self, name):
        return self.registry.get(name, AnimationAsset).toComponent()


mainScene = Scene()

timmy = mainScene.addAnimatedEntity(
    Vector3f(0, 0, 0),
    Vector3f(0, 0, 0),
    Vector3f(0.5),
    "core:models:Jazz Dancing",
    "core:images:lola_diffuse",
    "core:animations:Jazz Dancing")
# mainScene.addBillboard(timmy, Vector3f(0, 1.5, 0), Vector3f(1, 0, 0), Vector2f(0.5, 0.5))

# mainScene.AddEntity(Vector3f(10, 0, 0), Vector3f(0, 0, 0), Vector3f(1), "core:models:grass", "core:images:missing")

mainScene.playAnimation(timmy, "Jazz Dancing")

