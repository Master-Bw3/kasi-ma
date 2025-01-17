package tree.maple.kasima.blocks

import com.mojang.serialization.MapCodec
import net.minecraft.block.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView
import tree.maple.kasima.blocks.RuneLog.Companion.RUNE
import tree.maple.kasima.items.KasimaItems
import tree.maple.kasima.api.registry.BlockTokenRegistry
import tree.maple.kasima.spellEngine.compiler.*


class RuneCore(settings: Settings) : Block(settings), AxeMineable {

    init {
        defaultState = defaultState
            .with(RUNE, 0)
            .with(Properties.POWERED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(RUNE)
        builder.add(Properties.POWERED)
        builder.add(Properties.AXIS)
        super.appendProperties(builder)
    }

    override fun getCodec(): MapCodec<RuneCore>? {
        return createCodec(::RuneCore)
    }

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (player.isHolding(KasimaItems.CHISEL)) {
            return ActionResult.PASS
        }

        if (!world.isClient) {
            runProgram(world, pos)
        }

        return ActionResult.SUCCESS
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random?
    ): BlockState {
        val powered = state.get(Properties.POWERED) as Boolean

        if (world.isReceivingRedstonePower(pos) && !powered) {
            if (!world.isClient) runProgram(world as World, pos)

            return state.with(Properties.POWERED, true)
        }

        if (!world.isReceivingRedstonePower(pos) && powered) {
            return state.with(Properties.POWERED, false)
        }

        return state
    }

    fun runProgram(world: World, corePos: BlockPos) {
        //find where tree starts
        val axis = world.getBlockState(corePos).get(Properties.AXIS)

        val startDirection: Direction? = axis.directions.firstOrNull { direction ->
            val pos = corePos.add(direction.vector)
            BlockTokenRegistry.containsId(Registries.BLOCK.getId(world.getBlockState(pos).block))
        }


        if (startDirection != null) {
            val tokens = tokenizeTree(
                world,
                corePos.add(startDirection.vector),
                startDirection,
            )


            val result = try {
                val x = compileAndRun(tokens)
                Text.literal(x.toString())
            } catch (e: Throwable) {
                e.printStackTrace()
                if (e is CompilerError.SyntaxError) {
                    e.error
                } else {
                    Text.literal(e.message.toString())
                }
            }

            world.server?.playerManager?.broadcast(result, false)
        }

    }

    fun tokenizeTree(
        world: World,
        pos: BlockPos,
        direction: Direction,
    ): List<Token> = listOf(Token.StartGroup).plus(tokenizeTreeRec(world, pos, direction, mutableSetOf()))


    private fun tokenizeTreeRec(
        world: World,
        pos: BlockPos,
        direction: Direction,
        visited: MutableSet<BlockPos>,
    ): List<Token> {
        val blockState = world.getBlockState(pos)
        val id = Registries.BLOCK.getId(world.getBlockState(pos).block)

        return if (!BlockTokenRegistry.containsId(id) || pos in visited) {
            listOf(Token.EndGroup)
        } else {
            visited.add(pos)

            val token = BlockTokenRegistry[id]!!.token
            val axis = blockState.get(Properties.AXIS)

            val nextBlockDirection = determineDirection(direction, axis, pos, world)
            val nextBlockPos = pos.add(nextBlockDirection.vector)
            val isGroupStart = axis != direction.axis


            if (isGroupStart) {
                listOf(Token.StartGroup, token)
                    .plus(tokenizeTreeRec(world, nextBlockPos, nextBlockDirection, visited))
                    .plus(tokenizeTreeRec(world, pos.add(direction.vector), direction, visited))
            } else {
                listOf(token)
                    .plus(tokenizeTreeRec(world, nextBlockPos, nextBlockDirection, visited))
            }
        }
    }

    private fun determineDirection(
        prevDirection: Direction,
        axis: Direction.Axis,
        pos: BlockPos,
        world: World
    ) = if (prevDirection.axis == axis) {
        prevDirection
    } else {
        axis.directions.firstOrNull { direction ->
            val offsetPos = pos.add(direction.vector)
            BlockTokenRegistry.containsId(Registries.BLOCK.getId(world.getBlockState(offsetPos).block))
        } ?: axis.positiveDirection
    }
}